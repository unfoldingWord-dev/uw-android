package peejweej.sideloading.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import peejweej.sideloading.R;

/**
 * Created by Fechner on 7/8/15.
 */
public class FilesListAdapter extends ArrayAdapter<File> {

    List<File> files;

    public FilesListAdapter(Context context, List<File> files) {
        super(context, R.layout.row_file, files);
        this.files = files;
    }

    @Override
    public File getItem(int position) {
        return files.get(position);
    }

    @Override
    public int getCount() {
        return files.size();
    }

    public void updateData(List<File> files){
        this.files = files;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final File currentRow = getItem(position);
        ViewHolderForGroup holder;
        if (view == null) {

            holder = new ViewHolderForGroup();
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.row_file, parent, false);

            holder.fileNameTextView = (TextView) view.findViewById(R.id.file_name_text_view);
            holder.fileLocationTextView = (TextView) view.findViewById(R.id.file_location_text_view);

            view.setTag(holder);
        } else {
            holder = (ViewHolderForGroup) view.getTag();
        }

        holder.fileNameTextView.setText(currentRow.getName());
        holder.fileLocationTextView.setText(currentRow.getPath());
        return view;
    }

    private class ViewHolderForGroup {

        private TextView fileNameTextView;
        private TextView fileLocationTextView;
    }
}
