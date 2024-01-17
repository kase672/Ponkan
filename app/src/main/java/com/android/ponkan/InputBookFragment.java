package com.android.ponkan;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


public class InputBookFragment extends Fragment {
    public InputBookFragment() {
        super(R.layout.fragment_input_book);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button SaveButton = view.findViewById(R.id.InputButton);
        SaveButton.setOnClickListener(new SaveButtonClickListener(view));
    }
    private class SaveButtonClickListener implements View.OnClickListener {
        private View fragmentView;

        SaveButtonClickListener(View view) {
            this.fragmentView = view;
        }
        @Override
        public void onClick(View view) {

            EditText inputEditText = fragmentView.findViewById(R.id.ListInput);
            //入力した文字を保存
            String newBookName = inputEditText.getText().toString();

            // 新しい書籍が空でない場合、書籍リストに追加
            if (!newBookName.isEmpty()) {
                // SQLiteデータベースヘルパークラスのインスタンスを作成
                DatabaseHelper dbHelper = new DatabaseHelper(requireContext());//newBookNameを入れる場所

                // SQLiteデータベースに新しい書籍を追加
                dbHelper.addBook(newBookName);

//                // 書籍リストにも追加
//                Map<String, Object> newBook = new HashMap<>();
//                newBook.put("name", newBookName);
//                bookList.add(newBook);
//
//                // RecyclerViewの更新を通知
//                adapter_Main.notifyDataSetChanged();
            }

            FragmentManager manager = getParentFragmentManager();
            manager.popBackStack();
        }
    }

}