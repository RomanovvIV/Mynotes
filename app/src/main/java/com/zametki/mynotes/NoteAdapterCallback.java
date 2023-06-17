package com.zametki.mynotes;

import android.content.Context;

public interface NoteAdapterCallback {

    void edit(int position);
    void changePinState(int position);
    void delete(int position);
    void share(Context context, int position);
}
