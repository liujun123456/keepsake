package com.shunlai.im.face;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shunlai.im.R;
import com.shunlai.im.utils.ScreenUtil;
import com.shunlai.im.utils.SoftKeyBoardUtil;

import java.util.ArrayList;


public class FaceFragment extends Fragment {

    RecyclerView rvFace;
    ArrayList<Emoji> emojiList;
    private OnEmojiClickListener listener;
    private int vMargin = 0;

    public static FaceFragment Instance() {
        FaceFragment instance = new FaceFragment();
        Bundle bundle = new Bundle();
        instance.setArguments(bundle);
        return instance;
    }

    public void setListener(OnEmojiClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onAttach(Activity activity) {
        if (activity instanceof OnEmojiClickListener) {
            this.listener = (OnEmojiClickListener) activity;
        }
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        emojiList = FaceManager.getEmojiList();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_face, container, false);
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = SoftKeyBoardUtil.getSoftKeyBoardHeight();
        view.setLayoutParams(params);
        rvFace = view.findViewById(R.id.rv_face);
        initViews();
        return view;
    }

    private void initViews() {
        vMargin = ScreenUtil.dip2px(getContext(),8f);
        rvFace.setLayoutManager(new GridLayoutManager(getContext(), 7));
        rvFace.setAdapter(new FaceAdapter());
    }

    class FaceAdapter extends RecyclerView.Adapter<FaceViewHolder> {

        @NonNull
        @Override
        public FaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = View.inflate(getContext(), R.layout.item_face, null);
            FaceViewHolder holder = new FaceViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull FaceViewHolder holder, int position) {
            holder.setData(emojiList.get(position),position);
            holder.itemView.setOnClickListener(v -> listener.onEmojiClick(emojiList.get(position)));
        }

        @Override
        public int getItemCount() {
            return emojiList.size();
        }
    }

    class FaceViewHolder extends RecyclerView.ViewHolder {
        ImageView iv;

        public FaceViewHolder(@NonNull View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.face_image);
        }

        void setData(Emoji emoji, int position) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) iv.getLayoutParams();
            if (emoji != null) {
                params.width = emoji.getWidth();
                params.height = emoji.getHeight();
            }
            if (position / 7 == 0) {
                params.setMargins(0, vMargin, 0, 0);
            } else {
                params.setMargins(0, vMargin, 0, vMargin);
            }
            iv.setLayoutParams(params);
            iv.setImageBitmap(emoji.getIcon());
        }
    }


    public interface OnEmojiClickListener {

        void onEmojiClick(Emoji emoji);
    }

}
