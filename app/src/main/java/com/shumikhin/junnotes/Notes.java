package com.shumikhin.junnotes;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Date;

class Notes implements Parcelable {
    private int noteIndex;
    private String titleNote;

    public String getDescriptionNote() {
        return descriptionNote;
    }

    public void setDescriptionNote(String descriptionNote) {
        this.descriptionNote = descriptionNote;
    }

    private String descriptionNote;
    private Date dateNote;

    public Notes(int noteIndex, String titleNote, String descriptionNote, Date dateNote) {
        this.noteIndex = noteIndex;
        this.titleNote = titleNote;
        this.descriptionNote = descriptionNote;
        this.dateNote = dateNote;
    }

    protected Notes(Parcel in) {
        noteIndex = in.readInt();
        titleNote = in.readString();
        descriptionNote = in.readString();
        dateNote = (java.util.Date) in.readSerializable();
    }

    public static final Creator<Notes> CREATOR = new Creator<Notes>() {
        @Override
        public Notes createFromParcel(Parcel in) {
            return new Notes(in);
        }

        @Override
        public Notes[] newArray(int size) {
            return new Notes[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(noteIndex);
        parcel.writeString(titleNote);
        parcel.writeString(descriptionNote);
        parcel.writeSerializable(dateNote);
    }

}
