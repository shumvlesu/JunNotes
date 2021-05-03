package com.shumikhin.junnotes.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NotesSourceFirebaseImpl implements NotesSource {

    //идентефикатор нашей коллекции записей
    private static final String NOTES_COLLECTION = "notes";
    //для отладки
    private static final String TAG = "[NtsSrcFirebaseImpl]";
    // База данных Firestore
    private final FirebaseFirestore store = FirebaseFirestore.getInstance();
    // Коллекция наших записок, мы их получили по наименованию константы CARDS_COLLECTION - cards
    private final CollectionReference collection = store.collection(NOTES_COLLECTION);
    // Загружаемый список записок
    private List<NotesData> notesData = new ArrayList<NotesData>();


    //Метод get класса коллекции получает данные. Если требуется отсортировать эти данные, то
    //вызываем метод orderBy. Мы получаем типизированный класс Task с типом возврата
    //QuerySnapshot, который является результатом запроса. На этот Task можно повесить слушателя при
    //успешной и неуспешной работе задачи. Если задача была выполнена успешно, это ещё не значит, что
    //у нас есть правильный результат. Поэтому необходимо проверить, что задача выполнилась успешно и
    //хранит в себе правильный результат, за это отвечает метод isSuccessful().
    //Результат — это коллекция документов, где каждый документ — словарь с ключом-значением
    //(Map<String, Object>). Само преобразование между словарём и объектом вынесено в отдельный
    //класс, чтобы не засорять логику класса работы с Firestore.

    @Override
    public NotesSource init(final NotesSourceResponse notesSourceResponse) {
        // Получить всю коллекцию, отсортированную по полю «Дата»
        collection.orderBy(NoteDataMapping.Fields.DATE, Query.Direction.DESCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() { //addOnCompleteListener возвращает task
                    // При удачном считывании данных загрузим список карточек
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            notesData = new ArrayList<NotesData>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> doc = document.getData();
                                String id = document.getId();
                                NotesData noteData = NoteDataMapping.toNoteData(id, doc);
                                notesData.add(noteData);
                            }
                            Log.d(TAG, "success " + notesData.size() + " qnt");
                            notesSourceResponse.initialized(NotesSourceFirebaseImpl.this);
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "get failed with ", e);
                    }
                });
        return this;
    }

    @Override
    public NotesData getNotesData(int position) {
        return notesData.get(position);
    }

    @Override
    public int size() {
        if (notesData == null) {
            return 0;
        }
        return notesData.size();
    }

    @Override
    public void deleteNoteData(int position) {
        // Удалить документ с определённым идентификатором
        collection.document(notesData.get(position).getId()).delete();
        notesData.remove(position);
    }

    @Override
    public void updateNoteData(int position, NotesData notesData) {
        String id = notesData.getId();
        // Изменить документ по идентификатору
        collection.document(id).set(NoteDataMapping.toDocument(notesData));
    }

    @Override
    public void addNoteData(final NotesData notesData) {
        // Добавить документ
        collection.add(NoteDataMapping.toDocument(notesData))
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        notesData.setId(documentReference.getId());
                    }
                });
    }

    @Override
    public void clearNoteData() {
        for (NotesData cardData : notesData) {
            collection.document(cardData.getId()).delete();
        }
        notesData = new ArrayList<>();
    }

}
