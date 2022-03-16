package com.techload.madinatic.adapters;

import android.content.ContentValues;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.techload.madinatic.R;
import com.techload.madinatic.activities.MainActivity;
import com.techload.madinatic.data.InternalDataBase.InternalDataBaseSchemas;
import com.techload.madinatic.data.networking.DataCachingOperation;
import com.techload.madinatic.data.networking.Requests;
import com.techload.madinatic.data.networking.networkOperations.LimitedNetworkOperation;
import com.techload.madinatic.fragments.MediasPagerFragment;
import com.techload.madinatic.fragments.newsFeed.SeeCompletePost;
import com.techload.madinatic.lists_items.Media;
import com.techload.madinatic.lists_items.Post;
import com.techload.madinatic.custom_views.BookMarkView;
import com.techload.madinatic.utils.AppSettingsUtils;
import com.techload.madinatic.utils.GraphicUtils;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class PostsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //
    private ArrayList<Post> posts;


    class PostHolder extends RecyclerView.ViewHolder {
        TextView postOwnerName, postInfo, title, description, pageIndicator, id;
        View seeMore;
        ImageView postOwnerImg, zoomOutMedias, actionsOnPost;
        RecyclerView mediasPager;
        MediaSlideAdapter mediaSlideAdapter;
        BookMarkView bookMarkView;


        public PostHolder(View post) {
            super(post);
            //
            postOwnerImg = post.findViewById(R.id.post_owner_profile_picture);
            postOwnerName = post.findViewById(R.id.post_owner);
            postInfo = post.findViewById(R.id.post_info);
            //
            title = post.findViewById(R.id.post_title);
            description = post.findViewById(R.id.post_description);
            seeMore = post.findViewById(R.id.see_more);
            zoomOutMedias = post.findViewById(R.id.zoomOutMedias);
            pageIndicator = post.findViewById(R.id.pageIndicator);
            id = post.findViewById(R.id.post_id);
            actionsOnPost = post.findViewById(R.id.actions_on_post);
            //
            mediasPager = post.findViewById(R.id.media_pager);
            mediaSlideAdapter = new MediaSlideAdapter(new Media[]{}, pageIndicator);
            mediasPager.setLayoutManager(new LinearLayoutManager(mediasPager.getContext(), LinearLayoutManager.HORIZONTAL, false));
            mediasPager.setAdapter(mediaSlideAdapter);
            new PagerSnapHelper().attachToRecyclerView(mediasPager);
            //
            bookMarkView = post.findViewById(R.id.bookmarkView);
            if (MainActivity.token == null) bookMarkView.setVisibility(View.GONE);


            /*case of an unsigned user, the post has to be just like in light app theme*/
            if (MainActivity.token == null) {
                int textColor = ContextCompat.getColor(post.getContext(), R.color.black);
                postOwnerName.setTextColor(textColor);
                postInfo.setTextColor(textColor);
                zoomOutMedias.setColorFilter(0); // changing icon tint
                post.setBackgroundTintList(new ColorStateList(
                        new int[][]{},
                        new int[]{R.color.white}));
            }


        }
    }

    //
    class HintHolder extends RecyclerView.ViewHolder {
        TextView title, description;
        ImageView image;

        public HintHolder(@NonNull View container) {
            super(container);
            title = container.findViewById(R.id.hint_title);
            description = container.findViewById(R.id.hint_description);
            image = container.findViewById(R.id.hint_image);
        }
    }

    //
    public PostsListAdapter(ArrayList<Post> posts) {
        this.posts = posts;
    }

    @Override
    public int getItemViewType(int position) {
        if (posts.get(position).type == Post.TYPE_HINT)
            return Post.TYPE_HINT;
        else return Post.TYPE_POST;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == Post.TYPE_HINT) {
            return new HintHolder(LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.news_feed_hint, parent, false));
        }

        return new PostHolder(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.post_news_feed, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        final Post current = posts.get(position);

        if (holder instanceof HintHolder) {
            HintHolder hintHolder = (HintHolder) holder;
            hintHolder.title.setText(current.title);
            String textColor;
            if (MainActivity.token == null) {
                textColor = "#000000";
            } else {
                textColor = ((MainActivity.appTheme == AppSettingsUtils.APPLICATION_DARK_THEME) ? "#ffffff" : "#000000");
            }
            hintHolder.description.setText(Html.fromHtml(current.description
                    + "<br><br><font color='" + textColor
                    + "'>" + current.datePosted + "<font>"));

            if (MainActivity.token == null) {
                hintHolder.title.setTextColor(((HintHolder) holder).title.getContext().getResources().getColor(R.color.black));
            }

        } else {
            PostHolder postHolder = ((PostHolder) holder);
            //
            if (MainActivity.token == null) {
                int textColor = postHolder.title.getContext().getResources().getColor(R.color.black);
                postHolder.title.setTextColor(textColor);
            }
            //
            postHolder.title.setText(current.title);
            //
            postHolder.description.setText(current.description);
            //
            postHolder.id.setText(postHolder.id.getContext().getString(R.string.id) + ": " + current.id);
            postHolder.postInfo.setText(AppSettingsUtils.formatDate(MainActivity.appLanguage, current.datePosted));
            postHolder.postOwnerName.setText(current.postOwnerName);
            postHolder.mediaSlideAdapter.updatePager(current.medias);
            //zooming out the medias pager into full screen
            if (!postHolder.zoomOutMedias.hasOnClickListeners()) {
                postHolder.zoomOutMedias.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainActivity.putFragmentInMainActivityContentAndAddToBackStack(new MediasPagerFragment(current.medias));
                    }
                });
            }
            //
            postHolder.seeMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.putFragmentInMainActivityContentAndAddToBackStack(new SeeCompletePost(current));
                }
            });
            //
            updateProfilePicture(postHolder.postOwnerImg, new String[]{current.postOwnerProfilePicture}, current.postOwnerName);

        }

    }

    public void updatePost(ArrayList<Post> posts) {
        this.posts = posts;
        notifyDataSetChanged();
    }

    private void updateProfilePicture(final ImageView profilePicture, final String[] path, final String postOwnerName) {

        if (path == null || path[0].isEmpty()) {
            profilePicture.setImageResource(R.drawable.algeria_flag);

        } else {
            /*if already cached*/
            if (path[0].contains("/data/data/com.techload.madinatic/cache")) {
                /*if it's already in the ram*/
                if (MainActivity.allAppImages.containsKey(path[0])) {
                    GraphicUtils.toCircledImage(profilePicture,
                            MainActivity.allAppImages.get(path[0]));


                } else {
                    /*if not in the ram load it and put it into the ram*/
                    Bitmap profPic = GraphicUtils.loadCompressedBitmap(10, path[0]);
                    MainActivity.allAppImages.put(path[0], profPic);
                    GraphicUtils.toCircledImage(profilePicture, profPic);
                }


            } else {
                /*if not cached then download it from network if possible
                 * then put it into the cache, update medias table, update report first pic,
                 *  and put it in the the ram & showing it to the user*/
                if (AppSettingsUtils.isConnectedToInternet(profilePicture.getContext())) {
                    new LimitedNetworkOperation(profilePicture.getContext()) {
                        @Override
                        protected void doInUiThread(int evolutionFlag, Object result) {
                            if (evolutionFlag == EvolutionFlag.IMAGE_IS_READY) {

                                new DataCachingOperation(profilePicture.getContext()) {
                                    @Override
                                    public void doWhenFileCached(String filePath) {

                                        /*updating medias table in DB*/
                                        ContentValues row = new ContentValues();
                                        row.put(InternalDataBaseSchemas.Posts.COLUMN_POST_OWNER_PROFILE_PICTURE_CACHED_PATH, filePath);
                                        MainActivity.writableDB.update(InternalDataBaseSchemas.Posts.TABLE_NAME,
                                                row,
                                                InternalDataBaseSchemas.Posts.COLUMN_POST_OWNER_NAME + " = ? ",
                                                new String[]{postOwnerName});
                                        //

                                        /*put it into the ram & showing it to the user*/
                                        Bitmap profPic = GraphicUtils.loadCompressedBitmap(10, filePath);
                                        MainActivity.allAppImages.put(filePath, profPic);
                                        GraphicUtils.toCircledImage(profilePicture, profPic);

                                        /*updating other elements with the same post owner*/

                                        for (int i = 0; i < posts.size(); i++) {
                                            Post current = posts.get(i);
                                            if (path[0].equals(current.postOwnerProfilePicture)) {
                                                current.postOwnerProfilePicture = filePath;
                                            }
                                        }


                                    }
                                }.cacheFileInBackground(DataCachingOperation.DataType.IMPORTANT_IMAGE, result);

                            } else if (evolutionFlag == EvolutionFlag.REQUEST_UNACCEPTED || evolutionFlag == EvolutionFlag.ERROR_MESSAGE) {
                                profilePicture.setImageResource(R.drawable.algeria_flag);
                            }
                        }
                    }.downloadImage(Requests.IMAGES + path[0],
                            null,
                            LimitedNetworkOperation.ThreadPriority.LOW,
                            false);
                }
            }
        }
    }

    @Override
    public int getItemCount() {

        return (posts != null && !posts.isEmpty()) ? posts.size() : 0;
    }


}//
