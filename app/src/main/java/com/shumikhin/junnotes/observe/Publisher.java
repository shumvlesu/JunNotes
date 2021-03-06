package com.shumikhin.junnotes.observe;

//У нас будет разовый наблюдатель, и мы каждый раз будем его регистрировать, поскольку фрагмент
//будет создаваться каждый раз при редактировании записи. После отсылки сообщения будем
//отписывать наблюдатель. Паттерн «Наблюдатель» мы рассматривали в факультативе шестого урока.

import com.shumikhin.junnotes.data.NotesData;

import java.util.ArrayList;
import java.util.List;

public class Publisher {

    private List<NotesDataObserver> observers; // Все обозреватели

    public Publisher() {
        observers = new ArrayList<>();
    }

    // Подписать
    public void subscribe(NotesDataObserver observer) {
        observers.add(observer);
    }

    // Отписать
    public void unsubscribe(NotesDataObserver observer) {
        observers.remove(observer);
    }

    // Разослать событие
    public void notifySingle(NotesData notesData) {
        for (NotesDataObserver observer : observers) {
            observer.updateNoteData(notesData);
            unsubscribe(observer);
        }
    }
}

