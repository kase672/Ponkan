package com.android.ponkan;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainListFragment extends Fragment {

    RecyclerView rvBook;
    public MainListFragment() {
        super(R.layout.fragment_main);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("MainListFragment","onViewCreated");

        rvBook = view.findViewById(R.id.rvBook);
        LinearLayoutManager layout = new LinearLayoutManager(getActivity());
        rvBook.setLayoutManager(layout);

        List<Map<String, Object>> bookList = createBookList();
        List<Map<String, Object>> buttonList = createAddButtonList();

        RecyclerListAdapter adapter_Main = new RecyclerListAdapter(bookList, buttonList);
        rvBook.setAdapter(adapter_Main);

        DividerItemDecoration decorator = new DividerItemDecoration(getActivity(), layout.getOrientation());
        rvBook.addItemDecoration(decorator);

        final Drawable deleteIcon = ContextCompat.getDrawable(getActivity(), R.drawable.action_delete);
        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAdapterPosition();

                RecyclerListAdapter adapter_Main = (RecyclerListAdapter) rvBook.getAdapter();
                if (position < adapter_Main.get_listData().size()) {
                    // 通常のリストアイテムは左にスワイプ
                    return makeMovementFlags(0, ItemTouchHelper.LEFT);
                } else {
                    // 追加ボタンはスワイプさせない
                    return makeMovementFlags(0, 0);
                }
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                int swipedPosition = viewHolder.getAdapterPosition();
                Log.d("onSwiped", "" + swipedPosition + "");
                RecyclerListAdapter adapter_Main = (RecyclerListAdapter) rvBook.getAdapter();
                // 登録とかするんだったらなにかのリストから削除をする処理はここ
                DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
                dbHelper.deleteBook(swipedPosition);

                MainListFragmentUpdata();

                // 削除されたことを知らせて反映させる。
     //           adapter_Main.notifyItemRemoved(swipedPosition);
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                View itemView = viewHolder.itemView;

                // キャンセルされた時
                if (dX == 0f && !isCurrentlyActive) {
                    clearCanvas(c, itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, false);
                    return;
                }

                ColorDrawable background = new ColorDrawable();
                background.setColor(Color.parseColor("#f44336"));
                background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                background.draw(c);

                int deleteIconTop = itemView.getTop() + (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                int deleteIconMargin = (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                int deleteIconLeft = itemView.getRight() - deleteIconMargin - deleteIcon.getIntrinsicWidth();
                int deleteIconRight = itemView.getRight() - deleteIconMargin;
                int deleteIconBottom = deleteIconTop + deleteIcon.getIntrinsicHeight();

                deleteIcon.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);
                deleteIcon.draw(c);
            }
        };
        new ItemTouchHelper(callback).attachToRecyclerView(rvBook);


    }

    private void clearCanvas(Canvas c, int left, int top, int right, int bottom) {
        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        c.drawRect(left, top, right, bottom, paint);
    }



    private List<Map<String, Object>> createBookList() {
        List<Map<String, Object>> bookList = new ArrayList<>();

        // SQLiteデータベースヘルパークラスのインスタンスを作成
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());

        // SQLiteから書籍情報を取得
        List<Map<String, Object>> dbBookList = dbHelper.getAllBooks();

        // 取得した書籍情報をリストに追加
        bookList.addAll(dbBookList);
        return bookList;
    }

    private List<Map<String, Object>> createAddButtonList() {
        List<Map<String, Object>> buttonList = new ArrayList<>();
        Map<String, Object> book = new HashMap<>();
        book.put("name", getString(R.string.add_button_text));
        buttonList.add(book);

        return buttonList;
    }

    /**
     * RecyclerViewのビューホルダクラス。
     */
    private class RecyclerListViewHolder extends RecyclerView.ViewHolder {
        /**
         * リスト1行分中で単語帳名を表示する画面部品。
         */
        public TextView _tvBookNameRow;
        public TextView _addButton;

        /**
         * コンストラクタ。
         *
         * @param itemView リスト1行分の画面部品。
         */
        public RecyclerListViewHolder(View itemView) {
            super(itemView);
            _tvBookNameRow = itemView.findViewById(R.id.tvBookNameRow);
            _addButton = itemView.findViewById(R.id.addButton);

        }
    }

    /**
     * RecyclerViewのアダプタクラス。
     */
    private class RecyclerListAdapter extends RecyclerView.Adapter<RecyclerListViewHolder> {

        /**
         * onCreateViewHolderの引数positionへ出力する値
         */

        static final int STRING_BOOK = 0;
        static final int STRING_BUTTON = 1;

        /**
         * リストデータを保持するフィールド。createBookListのデータなど
         */
        private final List<Map<String, Object>> _listData;
        private final List<Map<String, Object>> _buttonData;

        public List<Map<String, Object>> get_listData(){
            return this._listData;
        }


        /**
         * コンストラクタ。
         *
         * @param listData リストデータ。
         */
        public RecyclerListAdapter(List<Map<String, Object>> listData, List<Map<String, Object>> buttonData) {
            _listData = listData;
            _buttonData = buttonData;
//            this._listData = listData;
//            this._buttonData = buttonData;
        }

        /**
         * row.xml全体の画面部品を取得
         */
        @NonNull
        @Override
        public RecyclerListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            Log.d("inflater", "" + inflater + "");
            View view;

            switch (viewType) {
                case STRING_BOOK:
                    view = inflater.inflate(R.layout.row, parent, false);
                    break;


                case STRING_BUTTON:
                    view = inflater.inflate(R.layout.add_row, parent, false);
                    break;
                /*viewがnullのときの動作が必要？*/
                default:
                    throw new IllegalArgumentException("Invalid view type");

            }
            RecyclerListViewHolder holder = new RecyclerListViewHolder(view);
            return holder;
            //return new RecyclerListViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerListViewHolder holder, int position) {


            if (position < _listData.size()) {
                Map<String, Object> listItem = _listData.get(position);
                String BookName_list = (String) listItem.get("name");
                holder._tvBookNameRow.setText(BookName_list);

            } else {
                // リストデータの範囲外の場合にのみボタンデータから取得するように修正
                int buttonPosition = position - _listData.size();
                if (buttonPosition < _buttonData.size()) {
                    Map<String, Object> buttonItem = _buttonData.get(buttonPosition);
                    String BookName_button = (String) buttonItem.get("name");
                    holder._addButton.setText(BookName_button);
                } else {
                    // エラー処理、例えばデータが存在しない場合にデフォルトのテキストを設定するなど
                    holder._addButton.setText("Default Button Text");
                }
            }
            if (position < _listData.size()) {
//                holder._tvBookNameRow.setOnClickListener(new ListItemClickListener());
            } else {
                // _addButtonをクリックしたときに処理が起こる
                holder._addButton.setOnClickListener(new ButtonClickListener());
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position < _listData.size()) {
                return STRING_BOOK;
            } else {
                return STRING_BUTTON;
            }
        }

        @Override
        public int getItemCount() {
            /*2つのリストを足したサイズを返すように修正した*/
            return _listData.size() + _buttonData.size();
        }
    }

    /**
     * リストをタップした時のリスナクラス。
     */



    /*private class ListItemClickListener implements View.OnClickListener {
        private int clickedPosition; // 追加

        @Override
        public void onClick(View view) {
            clickedPosition = rvBook.getChildAdapterPosition((View) view.getParent()); // クリックされた位置を取得

            SubListFragment subListFragment = new SubListFragment();

            // 位置情報をBundleにセットしてSubListFragmentに渡す
            Bundle bundle = new Bundle();
            bundle.putInt("clicked_position",clickedPosition);
            subListFragment.setArguments(bundle);

            FragmentManager manager = getParentFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setReorderingAllowed(true);
            transaction.addToBackStack("保留");
            transaction.replace(R.id.fragmentMainContainer, subListFragment);
            transaction.commit();
        }
    }*/

    private class ButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Log.d("MainList_onClick", ""+ view + "");
            InputBookFragment inputBookFragment = new InputBookFragment();
            FragmentManager manager = getParentFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setReorderingAllowed(true);
            transaction.addToBackStack("保留");
            transaction.replace(R.id.fragmentMainContainer, inputBookFragment);
            transaction.commit();
        }
    }

    public void MainListFragmentUpdata(){
        MainListFragment mainListFragment= new MainListFragment();
        FragmentManager manager = getParentFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragmentMainContainer, mainListFragment);
        transaction.commit();
    }


}