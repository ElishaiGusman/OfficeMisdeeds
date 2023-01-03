package com.elishai.officemisdeeds;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MisdeedFragment extends Fragment {

    private static final String ARG_MISDEED_ID = "misdeed_id";
    private static final String DIALOG_DATE    = "DialogDate";

    private static final int REQUEST_DATE       = 0;
    private static final int REQUEST_CONTACT    = 1;
    private static final int REQUEST_PHOTO      = 2;


    private Misdeed mMisdeed;
    private File mPhotoFile;
    private EditText mTitleField;
    private CheckBox mSolvedCheckBox;
    private Button mDateButton;
    private Button mSuspectButton;
    private Button mReportButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;

    public static MisdeedFragment newInstance(UUID misdeedId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_MISDEED_ID, misdeedId);

        MisdeedFragment fragment = new MisdeedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        UUID misdeedId = (UUID) getArguments().getSerializable(ARG_MISDEED_ID);
        mMisdeed = MisdeedManager.get(getActivity()).getMisdeed(misdeedId);
        mPhotoFile = MisdeedManager.get(getActivity()).getPhotoFile(mMisdeed);
    }

    @Override
    public void onPause() {
        super.onPause();

        MisdeedManager.get(getActivity()).updateMisdeed(mMisdeed);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mMisdeed.getSuspect() != null) {
            mSuspectButton.setText(mMisdeed.getSuspect());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_misdeed, container, false);

        mTitleField = (EditText) view.findViewById(R.id.misdeed_title);
        mTitleField.setText(mMisdeed.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //TO-DO
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mMisdeed.setTitle(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //TO-DO
            }
        });


        mDateButton = (Button) view.findViewById(R.id.misdeed_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                DateSelectorFragment dialog = DateSelectorFragment.newInstance(mMisdeed.getDate());
                dialog.setTargetFragment(MisdeedFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mSolvedCheckBox = (CheckBox) view.findViewById(R.id.misdeed_solved);
        mSolvedCheckBox.setChecked(mMisdeed.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                mMisdeed.setSolved(isChecked);
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton = (Button) view.findViewById(R.id.misdeed_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        if(mMisdeed.getSuspect() != null) {
            mSuspectButton.setText(mMisdeed.getSuspect());
        }

        mReportButton = (Button) view.findViewById(R.id.misdeed_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, getMisdeedReport());
                intent.putExtra(Intent.EXTRA_SUBJECT,
                        getString(R.string.report_subject));
                intent = Intent.createChooser(intent, getString(R.string.send_report));
                startActivity(intent);
                /*second option with IntentBuilder
                    Intent intent = ShareCompat.IntentBuilder.from(getActivity())
                    .setType("text/plain")
                    .setChooserTitle(getString(R.string.send_report))
                    .setSubject(getString(R.string.report_subject))
                    .setText(getMisdeedReport())
                    .createChooserIntent();
                    startActivity(intent);
                 */
            }
        });
        //if there is no contact apps in the device
        PackageManager packageManager = getActivity().getPackageManager();
        if(packageManager.resolveActivity(pickContact,
                PackageManager.MATCH_DEFAULT_ONLY) == null) {
                mSuspectButton.setEnabled(false);
        }

        mPhotoButton = (ImageButton) view.findViewById((R.id.misdeed_camera));
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        boolean canTakePhoto = (mPhotoFile != null) &&
                (captureImage.resolveActivity(packageManager) != null);

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (canTakePhoto) {
                    Uri uri = FileProvider.getUriForFile(getActivity(), "com.elishai.officemisdeeds.fileprovider",
                            mPhotoFile);
                    captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    List<ResolveInfo> cameraActivities = getActivity()
                            .getPackageManager().queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo activity : cameraActivities) {
                        getActivity().grantUriPermission(activity.activityInfo.packageName,
                                uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    }
                    startActivityForResult(captureImage, REQUEST_PHOTO);
                }
            }
        });
        mPhotoView = (ImageView) view.findViewById(R.id.misdeed_photo);
        updatePhotoView();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if(requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DateSelectorFragment.EXTRA_DATE);
            mMisdeed.setDate(date);
            updateDate();

        } else if(requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();
            //Determining the fields whose values should be
            //returned by the request.
            String[] queryFields = new String[] {
                    ContactsContract.Contacts.DISPLAY_NAME
            };
            //Making a request - contactUri here acts as a "where" condition
            Cursor cursor = getActivity().getContentResolver()
                    .query(contactUri, queryFields, null, null, null);
            try {
                //Checking of getting results
                if(cursor.getCount() == 0) {
                    return;
                }
                //Extracting the first data column - the name of a suspect.
                cursor.moveToFirst();
                String suspect = cursor.getString(0);
                mMisdeed.setSuspect(suspect);
                mSuspectButton.setText(suspect);
            }
            finally {
                cursor.close();
            }
        }  else if(requestCode == REQUEST_PHOTO) {
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "com.elishai.officemisdeeds.fileprovider",
                    mPhotoFile);

            getActivity().revokeUriPermission(uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updatePhotoView();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_misdeed_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_misdeed:
                MisdeedManager.get(getActivity()).deleteMisdeed(mMisdeed.getId());

                Intent intent = new Intent(getActivity(), MisdeedListActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateDate() {
        mDateButton.setText(getDateFormat());
    }

    private CharSequence getDateFormat() {
        return DateFormat.format("EEE, MMM dd, yyyy.", mMisdeed.getDate());
    }

    private String getMisdeedReport() {
        String solvedStatus = null;

        if(mMisdeed.isSolved()) {
            solvedStatus = getString(R.string.report_solved);
        } else {
            solvedStatus = getString(R.string.report_unsolved);
        }

        String dateString   = getDateFormat().toString();

        String suspect      = mMisdeed.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.report_no_suspect);
        } else {
            suspect =   getString(R.string.report_suspect, suspect);
        }

        String report = getString(R.string.misdeed_report,
                mMisdeed.getTitle(), dateString, solvedStatus, suspect);

        return report;
    }

    private void updatePhotoView() {
        if(mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = ImageUtils.getScaledBitmap(mPhotoFile.getPath(),
                    getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }
}