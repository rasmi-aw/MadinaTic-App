package com.techload.madinatic.adapters;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.techload.madinatic.R;
import com.techload.madinatic.activities.MainActivity;
import com.techload.madinatic.data.InternalDataBase.InternalDataBaseSchemas;
import com.techload.madinatic.data.networking.Requests;
import com.techload.madinatic.data.networking.networkOperations.LimitedNetworkOperation;
import com.techload.madinatic.lists_items.Media;
import com.techload.madinatic.utils.GraphicUtils;

import java.util.ArrayList;


public class MediaSlideAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Media[] medias;
    private TextView pageIndicator;
    private AudioManager audioManager;


    public MediaSlideAdapter(Media[] medias, TextView pageIndicator) {
        this.medias = medias;
        this.pageIndicator = pageIndicator;
    }

    public MediaSlideAdapter(ArrayList<Media> medias, TextView pageIndicator) {
        this.medias = new Media[medias.size()];
        for (int i = 0; i < medias.size(); i++) {
            this.medias[i] = medias.get(i);
        }
        this.pageIndicator = pageIndicator;
    }


    //
    private class ImageHolder extends RecyclerView.ViewHolder {
        ImageView image;
        ProgressBar circularProgress;
        TextView progressMessage;
        ViewGroup container;

        public ImageHolder(@NonNull ViewGroup container) {
            super(container);
            this.container = container;
            image = container.findViewById(R.id.image);
            circularProgress = container.findViewById(R.id.progress_circular);
            progressMessage = container.findViewById(R.id.progress_message);
        }
    }
    //

    private class VideoHolder extends RecyclerView.ViewHolder {

        // controls
        VideoView videoView;
        MediaPlayer mediaPlayer;
        View container;
        //

        int videoLengthInSeconds;

        public VideoHolder(@NonNull View container) {
            super(container);
            this.container = container;

            videoView = container.findViewById(R.id.video);
            //
            videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    /*skipping the can't play window*/
                    return true;
                }
            });


            //

            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    //
                    videoView.seekTo(100);
                    mediaPlayer = mp;
                    videoLengthInSeconds = (videoView.getDuration() / 1000);
                    videoView.start();
                }
            });
            //
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.seekTo(0);

                }
            });
        }


        /**
         *
         */



    }
    //


    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder instanceof VideoHolder){
            ((VideoHolder) holder).videoView.pause();
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (medias[position].type == Media.TYPE_IMAGE) return Media.TYPE_IMAGE;
        else return Media.TYPE_VIDEO;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == Media.TYPE_IMAGE)
            return new ImageHolder(((ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.media_image, parent, false)));

        return new VideoHolder((ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.media_video, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //
        if (medias == null || medias.length <= 1) pageIndicator.setVisibility(View.GONE);
        else pageIndicator.setVisibility(View.VISIBLE);


        //case of image
        if (holder instanceof ImageHolder) {
            dealWithImage(medias[position], ((ImageHolder) holder));
        } else {
            dealWithVideo(medias[position], ((VideoHolder) holder));
        }
    }

    @Override
    public int getItemCount() {
        return (medias == null) ? 0 : medias.length;
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (pageIndicator.getVisibility() == View.VISIBLE)
            pageIndicator.setText((holder.getAdapterPosition() + 1) + "/" + medias.length);
    }

    //
    public void updatePager(Media[] medias) {
        this.medias = medias;
        notifyDataSetChanged();
    }

    //
    public void updatePager(ArrayList<Media> medias) {
        this.medias = new Media[medias.size()];
        for (int i = 0; i < medias.size(); i++) {
            this.medias[i] = medias.get(i);
        }
        notifyDataSetChanged();
    }

    //
    private void dealWithImage(final Media media, final ImageHolder holder) {

        //case if the image already downloaded
        if (media.path != null)
            if (media.cached) {

                holder.container.removeView(holder.circularProgress);
                holder.container.removeView(holder.progressMessage);

                // case if image already exists in ram
                if (MainActivity.allAppImages.containsKey(media.path)) {
                    holder.image.setImageBitmap(MainActivity.allAppImages.get(media.path));
                } else {

                    Bitmap bitmap = GraphicUtils.loadCompressedBitmap(2, media.path);
                    MainActivity.allAppImages.put(media.path, bitmap);
                    holder.image.setImageBitmap(bitmap);
                }

            } else

                new LimitedNetworkOperation(holder.image.getContext()) {

                    @Override
                    protected void doInUiThread(int evolutionFlag, Object result) {
                        //

                        if (evolutionFlag == EvolutionFlag.IMAGE_IS_READY) {


                        } else if (evolutionFlag == EvolutionFlag.IMAGE_CACHED) {

                            //updating the media element with the cached path
                            ContentValues row = new ContentValues();
                            row.put(InternalDataBaseSchemas.Medias.COLUMN_CACHED_PATH, result.toString());
                            MainActivity.writableDB.update(InternalDataBaseSchemas.Medias.TABLE_NAME,
                                    row,
                                    InternalDataBaseSchemas.Medias.COLUMN_PATH + " = ?",
                                    new String[]{media.path});
                            //
                            media.path = ((String) result);
                            Bitmap bitmap = GraphicUtils.loadCompressedBitmap(2, media.path);
                            MainActivity.allAppImages.put(media.path, bitmap);
                            holder.image.setImageBitmap(bitmap);
                            //
                            holder.container.removeView(holder.circularProgress);
                            holder.container.removeView(holder.progressMessage);

                        } else if (evolutionFlag == EvolutionFlag.ERROR_MESSAGE || evolutionFlag == EvolutionFlag.REQUEST_UNACCEPTED) {
                            holder.container.removeView(holder.circularProgress);
                            holder.container.removeView(holder.progressMessage);
                        }

                    }
                }.downloadImage((Requests.IMAGES + media.path).replace("\\", "/"), null,
                        LimitedNetworkOperation.ThreadPriority.LOW,
                        true
                );
    }

    //
    private void dealWithVideo(Media media, final VideoHolder holder) {
        //
        holder.videoView.setVideoPath(Requests.VIDEOS+media.path);


    }

}
