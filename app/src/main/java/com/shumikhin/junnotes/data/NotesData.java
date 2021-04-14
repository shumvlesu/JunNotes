package com.shumikhin.junnotes.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NotesData implements Parcelable {

    private String titleNote;
    private String descriptionNote;
    private Date dateNote;

    public NotesData(String titleNote, String descriptionNote, Date dateNote) {
        this.titleNote = titleNote;
        this.descriptionNote = descriptionNote;
        this.dateNote = dateNote;
    }

    protected NotesData(Parcel in) {
        titleNote = in.readString();
        descriptionNote = in.readString();
        dateNote = (java.util.Date) in.readSerializable();
    }

    public static final Creator<NotesData> CREATOR = new Creator<NotesData>() {
        @Override
        public NotesData createFromParcel(Parcel in) {
            return new NotesData(in);
        }

        @Override
        public NotesData[] newArray(int size) {
            return new NotesData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(titleNote);
        parcel.writeString(descriptionNote);
        parcel.writeSerializable(dateNote);
    }

    public String getDescriptionNote() {
        return descriptionNote;
    }

    public String getTitleNote() {
        return titleNote;
    }

    public String getDateNote() {
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("E dd.MM.yyyy 'Время:' hh:mm:ss");
        return String.valueOf(formatForDateNow.format(dateNote));
    }

}
