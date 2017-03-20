package com.codepath.nytreader.dialogs;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.nytreader.R;
import com.codepath.nytreader.models.Settings;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cz.msebera.android.httpclient.util.TextUtils;


public class SettingsFragment extends DialogFragment {

    @BindView(R.id.etStartDate)
    EditText etStartDate;
    @BindView(R.id.spOrder)
    Spinner spOrder;
    @BindView(R.id.cbArts)
    CheckBox cbArts;
    @BindView(R.id.cbFashion)
    CheckBox cbFashion;
    @BindView(R.id.cbSport)
    CheckBox cbSports;

    @BindView(R.id.btnCancel)
    TextView btnCancel;
    @BindView(R.id.btnOk)
    TextView btnSave;

    private Unbinder unbinder;
    private Settings settings;

    public static SettingsFragment newInstance(Settings settingsObj) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putParcelable("settingsObj", Parcels.wrap(settingsObj));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Fetch arguments from bundle and set title
        settings = (Settings) Parcels.unwrap(getArguments().getParcelable("settingsObj"));

        if (settings == null) {
            settings = new Settings();
        }
        fillData();

        spOrder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedValue = spOrder.getSelectedItem().toString();
                settings.setSortOrder(selectedValue);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        etStartDate.setOnClickListener(v -> showDatePickerDialog());

        cbArts.setOnClickListener(v -> onCheckboxClicked(v));
        cbFashion.setOnClickListener(v -> onCheckboxClicked(v));
        cbSports.setOnClickListener(v -> onCheckboxClicked(v));
        btnSave.setOnClickListener(v -> onSettingsApply(v));
        btnCancel.setOnClickListener(v -> onSettingsCancel(v));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void fillData() {
        if (settings != null) {
            if (!TextUtils.isEmpty(settings.getStartDateDisplay())) {
                etStartDate.setText(settings.getStartDateDisplay());
            }
            String sortOrder = settings.getSortOrder();
            if (!TextUtils.isEmpty(sortOrder)) {
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.order_values, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spOrder.setAdapter(adapter);
                if (!sortOrder.equals(null)) {
                    int spinnerPosition = adapter.getPosition(sortOrder);
                    spOrder.setSelection(spinnerPosition);
                }
            }

            ArrayList<String> deskValues = settings.getDeskValues();
            if (deskValues != null) {
                cbFashion.setChecked(deskValues.contains(this.getString(R.string.cbFashionText)));
                cbArts.setChecked(deskValues.contains(this.getString(R.string.cbArtsText)));
                cbSports.setChecked(deskValues.contains(this.getString(R.string.cbSportsText)));
            }
        }
    }

    private void showDatePickerDialog() {

        Date startdate = null;
        if(settings!=null) {
        startdate=settings.getStartDate();
        }
        DatePickerFragment dpf = DatePickerFragment.newInstance(startdate);
        dpf.setCallBack(onDate);
        dpf.show(getFragmentManager().beginTransaction(), "DatePickerFragment");

    }

    DatePickerDialog.OnDateSetListener onDate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            etStartDate.setText(String.valueOf(year) + "-" + String.valueOf(monthOfYear)
                    + "-" + String.valueOf(dayOfMonth));

            final Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, monthOfYear);
            c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            settings.setStartDate(c.getTime());
        }
    };

    public void onSettingsCancel(View v) {
        dismiss();
    }

    public void onSettingsApply(View v) {
        if (settings != null) {
            //  settings.writeSettings(getFilesDir());
            Toast.makeText(getActivity(), "Settings saved", Toast.LENGTH_SHORT).show();

            EditSettingsDialogListener listener = (EditSettingsDialogListener) getActivity();
            listener.onFinishEditDialog(settings);
            // Close the dialog and return back to the parent activity
            dismiss();
        }
    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();
        ArrayList<String> descValues = settings.getDeskValues();
        // Check which checkbox was clicked
        String value = "";
        switch (view.getId()) {
            case R.id.cbSport:
                value = this.getString(R.string.cbSportsText);
                break;
            case R.id.cbArts:
                value = this.getString(R.string.cbArtsText);
                break;
            case R.id.cbFashion:
                value = this.getString(R.string.cbFashionText);
                break;
        }

        if (!TextUtils.isEmpty(value)) {
            if (checked) {
                descValues = AddToArrayList(descValues, value);
            } else {
                descValues = RemoveFromArrayList(descValues, value);
            }
        }
    }

    private ArrayList<String> AddToArrayList(ArrayList<String> list, String val) {
        if (list == null) {
            list = new ArrayList();
        }
        if (!list.contains(val.trim())) {
            list.add(val.trim());
        }

        return list;
    }

    private ArrayList<String> RemoveFromArrayList(ArrayList<String> list, String val) {
        if (list == null) {
            list = new ArrayList();
        }
        if (list.contains(val.trim())) {
            list.remove(val.trim());
        }

        return list;
    }

    public interface EditSettingsDialogListener {
        void onFinishEditDialog(Settings settingsObj);
    }

}
