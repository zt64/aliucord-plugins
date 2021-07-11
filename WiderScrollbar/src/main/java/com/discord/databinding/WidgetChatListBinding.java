package com.discord.databinding;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

public class WidgetChatListBinding implements ViewBinding {
    @NonNull
    public final RecyclerView a;
    @NonNull
    public final RecyclerView b;

    public WidgetChatListBinding(@NonNull RecyclerView recyclerView, @NonNull RecyclerView recyclerView2) {
        this.a = recyclerView;
        this.b = recyclerView2;
    }

    @Override // androidx.viewbinding.ViewBinding
    @NonNull
    public View getRoot() {
        return this.a;
    }
}
