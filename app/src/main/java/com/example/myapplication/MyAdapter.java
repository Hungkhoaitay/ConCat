package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ComponentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>
        implements Filterable {
    private Context context;
    private List<AppInfo> appInfos;
    private List<AppInfo> appInfosFull;
    private int buttonPos;
    private Intent intent;

    public MyAdapter(Context c, List<AppInfo> appInfos) {
        this.context = c;
        this.appInfos = appInfos;
        this.appInfosFull = new ArrayList<>(appInfos);
        AppCompatActivity ac = (AppCompatActivity) context;
        buttonPos = ac.getIntent().getIntExtra("Button Pos", -1);
        intent = new Intent(context, MainActivity.class);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row, parent, false);
        return new MyViewHolder(view);
    }

    /**
     * set the row of every rows in the RecycleView
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {
        AppInfo app = appInfos.get(position);

        holder.title.setText(app.getLabel(this.context));
        holder.icon.setImageDrawable(app.getIcon(this.context));

        holder.rowLayout.setOnClickListener(v -> {
            if (buttonPos != -1) {
                UserData.USERDATA.updateButton(buttonPos, app.getName());
                context.startActivity(intent);
                UserData.USERDATA.sendData((AppCompatActivity) context);
            } else {
                app.launch(context);
            }
        });
    }

    @Override
    public int getItemCount() {
        return appInfos.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<AppInfo> filteredApps = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredApps.addAll(appInfosFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                filteredApps = appInfosFull.stream()
                            .filter(app -> app.getLabel(context).toLowerCase().contains(filterPattern))
                            .collect(Collectors.toList());

            }

            FilterResults results = new FilterResults();
            results.values = filteredApps;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            appInfos.clear();
            appInfos.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        ImageView icon;
        LinearLayout rowLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textView);
            icon = itemView.findViewById(R.id.imageView);
            rowLayout = itemView.findViewById(R.id.rowLayout);
        }
    }
}
