package com.mislead.ikanxue.app.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.mislead.ikanxue.app.R;
import com.mislead.ikanxue.app.util.AndroidHelper;

/**
 * ThreadTypePopup
 *
 * @author Mislead
 *         DATE: 2015/7/18
 *         DESC:
 **/
public class ThreadTypePopup extends PopupWindow {

  private static String TAG = "ThreadTypePopup";

  private Context context;

  private RecyclerView recyclerView;

  private TypeSelectedListener selectedListener;

  public void setSelectedListener(TypeSelectedListener selectedListener) {
    this.selectedListener = selectedListener;
  }

  public ThreadTypePopup(Context context) {
    super(context);
    this.context = context;

    setContentView(initView());

    setBackgroundDrawable(new ColorDrawable(Color.WHITE));

    setOutsideTouchable(true);
  }

  private View initView() {
    View view = LayoutInflater.from(context).inflate(R.layout.view_recyclerview, null);

    recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

    GridLayoutManager manager = new GridLayoutManager(context, 4);
    recyclerView.setLayoutManager(manager);
    recyclerView.setHasFixedSize(true);

    recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
      @Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
          RecyclerView.State state) {
        int margin = AndroidHelper.dp2px((Activity) context, 4);
        outRect.set(margin, margin, margin, margin);
      }
    });

    final String data[] = context.getResources().getStringArray(R.array.topic_list);

    GridAdapter adapter = new GridAdapter(data);

    adapter.setListener(new GridAdapter.OnItemClickListener() {
      @Override public void itemClicked(int position) {
        if (selectedListener != null) {
          selectedListener.selected(data[position]);
        }
      }
    });

    recyclerView.setAdapter(adapter);

    return view;
  }

  public interface TypeSelectedListener {
    void selected(String s);
  }

  static class GridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private String[] ss;

    private OnItemClickListener listener;

    public GridAdapter(String[] ss) {
      this.ss = ss;
    }

    public void setListener(OnItemClickListener listener) {
      this.listener = listener;
    }

    @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

      View view =
          LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid, parent, false);
      ItemHolder holder = new ItemHolder(view);

      return holder;
    }

    @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

      ItemHolder itemHolder = (ItemHolder) holder;
      itemHolder.textView.setText(ss[position]);

      itemHolder.textView.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          if (listener != null) {
            listener.itemClicked(position);
          }
        }
      });
    }

    @Override public int getItemCount() {
      return ss.length;
    }

    public class ItemHolder extends RecyclerView.ViewHolder {

      public TextView textView;

      public ItemHolder(View itemView) {
        super(itemView);
        textView = (TextView) itemView.findViewById(R.id.textView);
      }
    }

    public interface OnItemClickListener {
      void itemClicked(int position);
    }
  }
}
