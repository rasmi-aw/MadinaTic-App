package com.techload.madinatic.fragments.helpers;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.techload.madinatic.R;
import com.techload.madinatic.activities.MainActivity;
import com.techload.madinatic.data.networking.DataCachingOperation;
import com.techload.madinatic.data.networking.RequestHeader;
import com.techload.madinatic.data.networking.Requests;
import com.techload.madinatic.data.networking.networkOperations.LimitedNetworkOperation;
import com.techload.madinatic.data.networking.services.NetworkingService;
import com.techload.madinatic.fragments.Edit_account;
import com.techload.madinatic.utils.AppSettingsUtils;
import com.techload.madinatic.utils.GraphicUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


public abstract class ChangeProfilePictureFragment extends TextEditableFragment {
    private AlertDialog popupWindow;
    private View.OnClickListener onColoredCircleClicked = null;
    //
    protected ImageView profilePicture;
    private ImageView profile_picture_popup;
    //
    private int profilePictureBackgroundResource = MainActivity.profilePictureBackgroundResource;
    private Bitmap photo;
    //
    int userDefaultProfilePic = R.drawable.user_male;


    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //
        if (!MainActivity.userIsMale) userDefaultProfilePic = R.drawable.user_female;
        //
        profilePicture = view.findViewById(R.id.profile_picture);
        profilePicture.setBackgroundResource(MainActivity.profilePictureBackgroundResource);

        if (MainActivity.token == null || MainActivity.profilePicturePath == null) {
            GraphicUtils.toCircledImage(profilePicture, getResources(), userDefaultProfilePic);
        } else
            GraphicUtils.toCircledImage(profilePicture, MainActivity.allAppImages.get(MainActivity.profilePicturePath));
        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeProfilePictureWindow(view);
            }
        });
    }

    @Override /*used to show the taken picture*/
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //

        if (requestCode == GraphicUtils.REQUEST.PHOTO_FROM_CAMERA && resultCode == Activity.RESULT_OK) {

            photo = (Bitmap) data.getExtras().get("data");
            GraphicUtils.toCircledImage(profile_picture_popup, photo);
        }
        if (requestCode == GraphicUtils.REQUEST.PHOTO_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            try {
                photo = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), data.getData());
                GraphicUtils.toCircledImage(profile_picture_popup, photo);
            } catch (IOException e) {

            }

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        popupWindow = null;
        onColoredCircleClicked = null;
        profilePicture = null;
        profile_picture_popup = null;

    }

    /**/
    protected void changeProfilePictureWindow(View parentFragment) {
        //
        popupWindow = (new AlertDialog.Builder(getContext())).create();
        popupWindow.setView(getLayoutInflater().inflate(R.layout.window_profile_picture_change, null));
        /**/
        popupWindow.getWindow().getDecorView().setBackground(null);
        /**/
        popupWindow.show();

        //
        profile_picture_popup = popupWindow.findViewById(R.id.profile_picture);
        profile_picture_popup.setBackgroundResource(profilePictureBackgroundResource);
        if (MainActivity.token == null || MainActivity.profilePicturePath == null) {
            GraphicUtils.toCircledImage(profile_picture_popup, getResources(), userDefaultProfilePic);

        } else
            GraphicUtils.toCircledImage(profile_picture_popup, MainActivity.allAppImages.get(MainActivity.profilePicturePath));
        //


        onColoredCircleClicked = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {

                    case R.id.red_circle:
                        profile_picture_popup.setBackgroundResource(R.drawable.circle_red);
                        profilePictureBackgroundResource = R.drawable.circle_red;
                        break;

                    case R.id.black_circle:
                        profile_picture_popup.setBackgroundResource(R.drawable.circle_black);
                        profilePictureBackgroundResource = R.drawable.circle_black;
                        break;

                    case R.id.gray_circle:
                        profile_picture_popup.setBackgroundResource(R.drawable.circle_gray);
                        profilePictureBackgroundResource = R.drawable.circle_gray;
                        break;

                    case R.id.blue_circle:
                        profile_picture_popup.setBackgroundResource(R.drawable.circle_blue);
                        profilePictureBackgroundResource = R.drawable.circle_blue;
                        break;

                    case R.id.light_gray_circle:
                        profile_picture_popup.setBackgroundResource(R.drawable.circle_light_gray);
                        profilePictureBackgroundResource = R.drawable.circle_light_gray;
                        break;

                    case R.id.green_circle:
                        profile_picture_popup.setBackgroundResource(R.drawable.circle_green);
                        profilePictureBackgroundResource = R.drawable.circle_green;
                        break;

                    case R.id.pink_circle:
                        profile_picture_popup.setBackgroundResource(R.drawable.circle_pink);
                        profilePictureBackgroundResource = R.drawable.circle_pink;
                        break;

                    case R.id.mauve_circle:
                        profile_picture_popup.setBackgroundResource(R.drawable.circle_mauve);
                        profilePictureBackgroundResource = R.drawable.circle_mauve;
                        break;

                    case R.id.orange_circle:
                        profile_picture_popup.setBackgroundResource(R.drawable.circle_orange);
                        profilePictureBackgroundResource = R.drawable.circle_orange;
                        break;

                    case R.id.blue_gray_circle:
                        profile_picture_popup.setBackgroundResource(R.drawable.circle_blue_gray);
                        profilePictureBackgroundResource = R.drawable.circle_blue_gray;
                        break;
                }
            }
        };


        popupWindow.findViewById(R.id.red_circle).setOnClickListener(onColoredCircleClicked);

        popupWindow.findViewById(R.id.black_circle).setOnClickListener(onColoredCircleClicked);

        popupWindow.findViewById(R.id.gray_circle).setOnClickListener(onColoredCircleClicked);

        popupWindow.findViewById(R.id.blue_circle).setOnClickListener(onColoredCircleClicked);

        popupWindow.findViewById(R.id.light_gray_circle).setOnClickListener(onColoredCircleClicked);

        popupWindow.findViewById(R.id.green_circle).setOnClickListener(onColoredCircleClicked);

        popupWindow.findViewById(R.id.pink_circle).setOnClickListener(onColoredCircleClicked);

        popupWindow.findViewById(R.id.mauve_circle).setOnClickListener(onColoredCircleClicked);

        popupWindow.findViewById(R.id.orange_circle).setOnClickListener(onColoredCircleClicked);

        popupWindow.findViewById(R.id.blue_gray_circle).setOnClickListener(onColoredCircleClicked);

        //cancel button
        popupWindow.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        //save profile picture button
        popupWindow.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                MainActivity.profilePictureBackgroundResource = profilePictureBackgroundResource;
                AppSettingsUtils.changeUserProfilePictureBackground();
                profile_picture_popup.setBackgroundResource(profilePictureBackgroundResource);
                profilePicture.setBackgroundResource(profilePictureBackgroundResource);
                popupWindow.dismiss();


                if (photo != null) {
                    if (AppSettingsUtils.isConnectedToInternet(getContext())) {

                        showProgressWindow(getString(R.string.connecting_to_server), false);
                        /*caching image then sending it */
                        new DataCachingOperation(getContext()) {
                            @Override
                            public void doWhenFileCached(final String filePath) {
                                //
                                new LimitedNetworkOperation(getContext()) {
                                    @Override
                                    protected void doInUiThread(int evolutionFlag, Object result) {
                                        if (evolutionFlag == EvolutionFlag.REQUEST_ACCEPTED) {
                                            NetworkingService.updateUserData(MainActivity.userData, getContext(), MainActivity.sharedPrefEditor);
                                            showResultsWindow(getString(R.string.profile_picture_updated), LoadingFragment.RESULT_WELL_DONE);
                                            GraphicUtils.toCircledImage(profilePicture, photo);


                                        }
                                        hideProgressWindow();
                                        photo = null;
                                    }
                                }.postMultiPartDataForm(Requests.EDIT_ACCOUNT,
                                        new RequestHeader(new RequestHeader.Property(RequestHeader.Field.AUTHORIZATION, "Bearer " + MainActivity.token)),
                                        LimitedNetworkOperation.ThreadPriority.LOW,
                                        new RequestHeader.Property("username", MainActivity.userData.getPseudo()),
                                        new RequestHeader.Property("email", MainActivity.userData.getEmail()),
                                        new RequestHeader.Property("phone_number", MainActivity.userData.getPhone()),
                                        new RequestHeader.Property("uploads", filePath));
                            }
                        }.cacheFileInBackground(DataCachingOperation.DataType.IMAGE, photo);

                    } else {
                        Toast.makeText(getContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                } else
                    Toast.makeText(getContext(), getString(R.string.select_photo), Toast.LENGTH_SHORT).show();


            }

        });

        //take photo
        popupWindow.findViewById(R.id.take_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), GraphicUtils.REQUEST.PHOTO_FROM_CAMERA);
            }
        });

        //select photo from gallery
        popupWindow.findViewById(R.id.select_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickIntent = new Intent(Intent.ACTION_PICK);
                pickIntent.setType("image/*");
                String[] mimeTypes = {"image/jpeg", "image/png"};
                pickIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                startActivityForResult(pickIntent, GraphicUtils.REQUEST.PHOTO_FROM_GALLERY);
            }
        });


    }

}//
