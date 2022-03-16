package com.techload.madinatic.fragments.reports;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.MultiAutoCompleteTextView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.techload.madinatic.R;
import com.techload.madinatic.activities.MainActivity;
import com.techload.madinatic.adapters.MediaSlideAdapter;
import com.techload.madinatic.adapters.reports.ProblemCategoriesAdapter;
import com.techload.madinatic.data.InternalDataBase.InternalDataBaseSchemas;
import com.techload.madinatic.data.networking.DataCachingOperation;
import com.techload.madinatic.data.networking.RequestHeader;
import com.techload.madinatic.data.networking.Requests;
import com.techload.madinatic.data.networking.networkOperations.LimitedNetworkOperation;
import com.techload.madinatic.data.networking.networkOperations.Reporting;
import com.techload.madinatic.fragments.MediasPagerFragment;
import com.techload.madinatic.fragments.helpers.LoadingFragment;
import com.techload.madinatic.fragments.helpers.TextEditableFragment;
import com.techload.madinatic.lists_items.Media;
import com.techload.madinatic.lists_items.Report;
import com.techload.madinatic.utils.AppSettingsUtils;
import com.techload.madinatic.utils.GraphicUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReportProblemScreen extends TextEditableFragment implements InternalDataBaseSchemas.Reports {
    //
    private TextInputEditText title, description;
    private MultiAutoCompleteTextView address;
    private TextInputLayout titleParent, descriptionParent, addressParent;
    private View addPhoto, addVideo;
    private PopupMenu photoMenu, videoMenu;
    private TextView saveReport, sendReport;
    private Spinner category;
    private RecyclerView mediasPager;
    private View removeMedia, mediasContainer, zoomMedias;
    //
    private ArrayList<Media> medias;
    private ArrayList<String> categories;
    private Report report;
    private char resultType;
    private String valTitle, valDescription, valAddress, valCategory, url;

    //

    public ReportProblemScreen() {
        //
        medias = new ArrayList<>();
        categories = new ArrayList<>();
        //
        report = null;
    }

    public ReportProblemScreen(Report report) {
        this();
        //
        this.report = report;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_report_problem_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //
        initialization(view);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        //
        mediasPager = null;
        title = null;
        description = null;
        address = null;
        titleParent = null;
        descriptionParent = null;
        addressParent = null;
        addPhoto = null;
        addVideo = null;
        photoMenu = null;
        videoMenu = null;
        saveReport = null;
        sendReport = null;
        category = null;
        removeMedia = null;
        mediasContainer = null;
        zoomMedias = null;
        valTitle = null;
        valDescription = null;
        valAddress = null;
        valCategory = null;
        url = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String photo = null;

        if (resultCode == Activity.RESULT_OK) {
            ((View) mediasPager.getParent()).setVisibility(View.VISIBLE);
            // in case we requested a photo
            if (requestCode < 130 && requestCode > 120) {

                if (requestCode == GraphicUtils.REQUEST.PHOTO_FROM_GALLERY) {
                    photo = "photo" + medias.size() + "-" + getString(R.string.app_name);
                    try {
                        final Bitmap img = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), data.getData());
                        MainActivity.allAppImages.put(photo, img);
                        medias.add(new Media(Media.TYPE_IMAGE, photo, true));
                        ((MediaSlideAdapter) mediasPager.getAdapter()).updatePager(medias);
                    } catch (IOException e) {
                    }


                } else if (requestCode == GraphicUtils.REQUEST.PHOTO_FROM_CAMERA) {
                    photo = "photo" + medias.size() + "-" + getString(R.string.app_name);
                    medias.add(new Media(Media.TYPE_IMAGE, photo, true));
                    MainActivity.allAppImages.put(photo, ((Bitmap) data.getExtras().get("data")));
                    ((MediaSlideAdapter) mediasPager.getAdapter()).updatePager(medias);

                }

            } else if (requestCode < 140 && requestCode > 130) {
                // in case we requested a video
                if (requestCode == GraphicUtils.REQUEST.VIDEO_FROM_CAMERA) {

                } else if (requestCode == GraphicUtils.REQUEST.VIDEO_FROM_GALLERY) {

                }
            }
            if (medias.size() > 0) {
                if (mediasContainer.getVisibility() == View.GONE)
                    mediasContainer.setVisibility(View.VISIBLE);

                if (removeMedia.getVisibility() == View.GONE)
                    removeMedia.setVisibility(View.VISIBLE);

                if (mediasPager.getVisibility() == View.GONE)
                    mediasPager.setVisibility(View.VISIBLE);

            }

            mediasPager.scrollToPosition(medias.size() - 1);
        }

    }

    private void initialization(View view) {
        //
        title = view.findViewById(R.id.report_title_editText);
        description = view.findViewById(R.id.report_description_editText);
        address = view.findViewById(R.id.report_address_editText);
        titleParent = view.findViewById(R.id.report_title_container);
        descriptionParent = view.findViewById(R.id.report_description_container);
        addressParent = view.findViewById(R.id.report_address_container);
        category = view.findViewById(R.id.report_category);
        //
        addPhoto = view.findViewById(R.id.add_photo);
        addVideo = view.findViewById(R.id.add_video);
        saveReport = view.findViewById(R.id.save);
        sendReport = view.findViewById(R.id.send_report);
        //
        category.getBackground().setColorFilter(getResources().getColor(R.color.gray), PorterDuff.Mode.SRC_ATOP);
        new LimitedNetworkOperation(getContext()) {
            @Override
            protected void doInUiThread(int evolutionFlag, Object result) {
                if (evolutionFlag == EvolutionFlag.JSON_IS_READY){
                    try {
                        JSONArray categos = new JSONObject(result.toString()).getJSONArray("categories");
                        for (int i = 0; i <categos.length() ; i++) {
                            categories.add(categos.getString(i).trim());
                        }
                        category.setAdapter(new ProblemCategoriesAdapter(MainActivity.mainFragmentActivity, categories));
                    } catch (JSONException e) {

                    }
                }
            }
        }.downloadText(Requests.GET_CATEGORIES,
                new RequestHeader(new RequestHeader.Property(RequestHeader.Field.AUTHORIZATION, "Bearer " + MainActivity.token)),
                LimitedNetworkOperation.ThreadPriority.LOW,false);
        //
        mediasContainer = view.findViewById(R.id.medias_container);
        removeMedia = view.findViewById(R.id.remove_media);
        removeMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!medias.isEmpty()) {
                    int position = ((LinearLayoutManager) mediasPager.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                    MainActivity.allAppImages.remove(medias.get(position).path);
                    medias.remove(position);
                    ((MediaSlideAdapter) mediasPager.getAdapter()).updatePager(medias);
                }

                if (medias.isEmpty()) {
                    removeMedia.setVisibility(View.GONE);
                    mediasPager.setVisibility(View.GONE);
                    mediasContainer.setVisibility(View.GONE);
                }
            }
        });
        if (medias.isEmpty()) removeMedia.setVisibility(View.GONE);

        //
        mediasPager = view.findViewById(R.id.media_pager);
        mediasPager.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mediasPager.setAdapter(new MediaSlideAdapter(medias, ((TextView) getView().findViewById(R.id.pageIndicator))));
        new PagerSnapHelper().attachToRecyclerView(mediasPager);

        /*case we want to edit an existing report*/
        if (report != null) {
            title.setText(report.title);
            description.setText(report.description);
            address.setText(report.address);
            if (report.category.contains("lairage"))
                category.setSelection(1);
            else if (report.category.contains("route"))
                category.setSelection(2);
            else if (report.category.toLowerCase().equals("autre"))
                category.setSelection(3);

            if (report.media != null && report.media.length > 0) {
                mediasPager.setAdapter(new MediaSlideAdapter(medias, ((TextView) view.findViewById(R.id.pageIndicator))));
            }

        }

        //
        if (medias.isEmpty()) {
            ((View) mediasPager.getParent()).setVisibility(View.GONE);
        }

        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //creating the menu to decide whether to select a photo from gallery or take a pic using the camera app
                if (medias.size() < 4) {
                    if (photoMenu == null) {
                        Context wrapper = new ContextThemeWrapper(getContext(), R.style.PopupMenuStyle);
                        photoMenu = new PopupMenu(wrapper, v);
                        photoMenu.getMenuInflater().inflate(R.menu.media_selection_way, photoMenu.getMenu());
                        photoMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {

                                if (item.getItemId() == R.id.from_camera) {
                                    /*taking a photo with camera*/
                                    startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE),
                                            GraphicUtils.REQUEST.PHOTO_FROM_CAMERA);

                                } else {

                                    /*picking a photo from gallery*/
                                    Intent pickIntent = new Intent(Intent.ACTION_PICK);
                                    pickIntent.setType("image/*");
                                    String[] mimeTypes = {"image/jpeg", "image/png"};
                                    pickIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                                    startActivityForResult(pickIntent, GraphicUtils.REQUEST.PHOTO_FROM_GALLERY);
                                }

                                return false;
                            }
                        });
                    }
                    photoMenu.show();
                }
                //

            }
        });
        //

        addVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (videoMenu == null) {
                    Context wrapper = new ContextThemeWrapper(getContext(), R.style.PopupMenuStyle);
                    videoMenu = new PopupMenu(wrapper, v);
                    videoMenu.getMenuInflater().inflate(R.menu.media_selection_way, videoMenu.getMenu());
                    videoMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            if (item.getItemId() == R.id.from_camera) {
                                /*taking a photo with camera*/
                                startActivityForResult(new Intent(MediaStore.ACTION_VIDEO_CAPTURE),
                                        GraphicUtils.REQUEST.VIDEO_FROM_CAMERA);

                            } else {

                                /*picking a photo from gallery*/
                                Intent pickIntent = new Intent(Intent.ACTION_PICK);
                                pickIntent.setType("video/*");
                                startActivityForResult(pickIntent, GraphicUtils.REQUEST.VIDEO_FROM_GALLERY);
                            }

                            return false;
                        }
                    });
                }
                videoMenu.show();

            }
        });
        // removing the error flag when typing
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                titleParent.setErrorEnabled(false);
                descriptionParent.setErrorEnabled(false);
                addressParent.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        title.addTextChangedListener(watcher);
        description.addTextChangedListener(watcher);
        address.addTextChangedListener(watcher);
        //address suggestions
        String[] addressSuggestions = new String[]{"Sidi bel Abbès", "Sidi bel Abbès, Centre ville", "Sidi bel Abbès, Wiam", "Sidi bel Abbès, Benhamouda",
                "Sidi bel Abbès, Sidi el djilali", "Sidi bel Abbès, Rue de Gambetta", "Sidi bel Abbès, Sorecor", "Sidi bel Abbès, Labrimer",
                "Sidi bel Abbès, Rue Soria Bendimred", "Sidi bel Abbès, Rochet", "Sidi bel Abbès, Cité Houari Boumediène", "Sidi bel Abbès, Cité Adda Boudjellal",
                "Sidi bel Abbès, Cité Makam El Chahid", "Sidi bel Abbès, Stade 24 février", "Sidi bel Abbès, Stade des trois frères Amiroch", "Sidi bel Abbès, Cité Tayebi El Arbi",
                "Sidi bel Abbès, Cité 400"
        };

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, addressSuggestions);
        address.setAdapter(arrayAdapter);
        int colorRes = (MainActivity.appTheme == AppSettingsUtils.APPLICATION_LIGHT_THEME) ?
                R.color.extra_light_gray : R.color.dark_gray;
        address.setDropDownBackgroundResource(colorRes);
        address.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        //
        View.OnClickListener saveOrSendListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard(getView());
                //

                url = Requests.ADD_NEW_REPORT;
                if (report != null && (report.state == Report.STATE_INCOMPLETE || report.state == Report.STATE_SENT))
                    url = Requests.EDIT_OR_COMPLETE_REPORT.replace("id", report.id + "");

                //
                valTitle = title.getText().toString().trim();
                valDescription = description.getText().toString().trim();
                valAddress = address.getText().toString().trim();
                //
                valCategory = categories.get(category.getSelectedItemPosition());

                //
                if (valTitle == null || valTitle.length() < 4) {
                    titleParent.setError(getString(R.string.error_too_short));
                    return;
                }

                if (valDescription == null || valDescription.length() < 10) {
                    descriptionParent.setError(getString(R.string.error_too_short));
                    return;
                }

                if (valAddress == null || valAddress.length() < 5) {
                    addressParent.setError(getString(R.string.error_too_short));
                    return;
                }
                /*save report in the data base*/
                if (v.getId() == saveReport.getId()) {

                    ContentValues row = new ContentValues();
                    row.put(COLUMN_REPORT_CATEGORY, valCategory);
                    row.put(COLUMN_TITLE, valTitle);
                    row.put(COLUMN_DESCRIPTION, valDescription);
                    row.put(COLUMN_ADDRESS, valAddress);
                    row.put(COLUMN_STATE, Report.STATE_SAVED + "");
                    row.put(COLUMN_BELONGS_TO_USER, 1);
                    if (report == null)
                        MainActivity.writableDB.insert(TABLE_NAME, null, row);
                    else if (report.state == Report.STATE_SAVED)
                        MainActivity.writableDB.update(TABLE_NAME, row, " _id = ?", new String[]{"" + report.id});

                    Toast.makeText(getContext(), getString(R.string.your_medias_not_saved_with_report), Toast.LENGTH_LONG).show();
                    MainActivity.fragmentManagerMainActivity.popBackStack();

                } else {
                    /*send the report if there's a photo at least and the user is connected to the internet*/
                    if (AppSettingsUtils.isConnectedToInternet(getContext())) {

                        if (medias.isEmpty() && (report == null || report.state == Report.STATE_SAVED)) {
                            Toast.makeText(getContext(), getString(R.string.one_photo_at_least), Toast.LENGTH_LONG).show();
                            return;
                        }
                        //
                        sendReport.setEnabled(false);
                        sendReport.setBackgroundResource(R.drawable.rectangle_rounded_gray);
                        //
                        if (isVisible())
                            showProgressWindow(getString(R.string.connecting_to_server), true);


                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... voids) {
                                /*caching images before sending them*/
                                for (int i = 0; i < medias.size(); i++) {
                                    Media current = medias.get(i);

                                     DataCachingOperation cache = new DataCachingOperation(getContext());
                                     current.path =  cache.putFileInCache(DataCachingOperation.DataType.IMAGE,
                                             MainActivity.allAppImages.get(current.path));

                                }
                                //
                                final RequestHeader.Property[] toBeSent = new RequestHeader.Property[medias.size() + 4];
                                for (int i = 0; i < medias.size(); i++) {
                                    Media current = medias.get(i);
                                    toBeSent[i] = new RequestHeader.Property("uploads", current.path);

                                }


                                //
                                toBeSent[medias.size()] = new RequestHeader.Property("title", valTitle);
                                toBeSent[medias.size() + 1] = new RequestHeader.Property("description", valDescription);
                                toBeSent[medias.size() + 2] = new RequestHeader.Property("address", valAddress);
                                toBeSent[medias.size() + 3] = new RequestHeader.Property("categorie", valCategory);

                                new LimitedNetworkOperation(getContext()) {
                                    @Override
                                    protected void doInUiThread(int evolutionFlag, Object result) {

                                        if (evolutionFlag == EvolutionFlag.JSON_IS_READY || evolutionFlag == EvolutionFlag.ERROR_MESSAGE) {
                                            if (evolutionFlag == EvolutionFlag.JSON_IS_READY) {
                                                if (report != null && report.state == Report.STATE_SAVED)
                                                    MainActivity.writableDB.delete(TABLE_NAME, COLUMN_ID + " = ? ", new String[]{"" + report.id});
                                                //
                                                if (report != null)
                                                    MainActivity.writableDB.delete(InternalDataBaseSchemas.Medias.TABLE_NAME,
                                                            InternalDataBaseSchemas.Medias.COLUMN_RELATED_TO_WHICH_TABLE + " = ? AND "
                                                                    + InternalDataBaseSchemas.Medias.COLUMN_RELATED_TO_WHICH_ROW + " = ?",
                                                            new String[]{TABLE_NAME, "" + report.id});
                                                //
                                                resultType = LoadingFragment.RESULT_WELL_DONE;
                                                new Reporting(MainActivity.mainFragmentActivity, MainActivity.readableDB, MainActivity.writableDB).loadUserReports();
                                                if (isVisible())
                                                    showResultsWindow(getString(R.string.report_sent), LoadingFragment.RESULT_WELL_DONE);
                                                else
                                                    Toast.makeText(MainActivity.mainFragmentActivity, getString(R.string.report_sent), Toast.LENGTH_SHORT).show();

                                            } else if (evolutionFlag == EvolutionFlag.ERROR_MESSAGE) {

                                                try {
                                                    String message = new JSONObject(((String) result)).toString();
                                                    resultType = LoadingFragment.RESULT_ERROR;
                                                    if (isVisible())
                                                        showResultsWindow(message, LoadingFragment.RESULT_ERROR);

                                                } catch (JSONException e) {

                                                }

                                            }

                                            if (isVisible()) {
                                                sendReport.setEnabled(true);
                                                sendReport.setBackgroundResource(R.drawable.rectangle_rounded_green);
                                            }
                                        }


                                    }
                                }.postMultiPartDataForm(url,
                                        new RequestHeader(new RequestHeader.Property(RequestHeader.Field.AUTHORIZATION, "Bearer " + MainActivity.token)),
                                        LimitedNetworkOperation.ThreadPriority.LOW,
                                        toBeSent);
                                return null;
                            }


                            @Override
                            protected void onPostExecute(Void aVoid) {
                                super.onPostExecute(aVoid);
                            }
                        }.execute();

                    } else
                        Toast.makeText(getContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();

                }

            }
        };
        //
        if (report != null) {

            if (report.state != Report.STATE_SAVED) {
                ((ViewGroup) saveReport.getParent()).removeView(saveReport);
            } else
                saveReport.setOnClickListener(saveOrSendListener);
            //
            switch (report.state) {
                case Report.STATE_SENT:
                    sendReport.setText(R.string.edit);
                    break;

                case Report.STATE_SAVED:
                    sendReport.setText(R.string.send);
                    break;

                case Report.STATE_INCOMPLETE:
                    sendReport.setText(R.string.complete);
                    break;
            }
        }
        //
        saveReport.setOnClickListener(saveOrSendListener);
        sendReport.setOnClickListener(saveOrSendListener);
        //
        zoomMedias = view.findViewById(R.id.zoomOutMedias);
        zoomMedias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!medias.isEmpty()) {
                    MainActivity.putFragmentInMainActivityContentAndAddToBackStack(new MediasPagerFragment(medias));
                }
            }
        });


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        if (medias != null) {
//            for (int i = 0; i < medias.size(); i++) {
//                Media current = medias.get(i);
//                MainActivity.allAppImages.remove(current.path);
//            }
//            medias.clear();
//
//        }

    }

    @Override
    public void doWhenResultsSeen() {
        if (resultType == RESULT_WELL_DONE) {
            if (isVisible()) MainActivity.fragmentManagerMainActivity.popBackStack();

        }
    }
}
