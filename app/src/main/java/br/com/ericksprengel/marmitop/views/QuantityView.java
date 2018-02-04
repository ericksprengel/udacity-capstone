package br.com.ericksprengel.marmitop.views;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.ericksprengel.marmitop.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class QuantityView extends FrameLayout {

    // layout configs

    // layout views
    @BindView(R.id.quantity_vw_quantity_textview) TextView mQuantity;
    @BindView(R.id.quantity_vw_quantity_add_floatingactionbutton) View mQuantityAdd;
    @BindView(R.id.quantity_vw_quantity_remove_floatingactionbutton) View mQuantityRemove;

    // view listeners
    OnQuantityListener mQuantityListener;

    public interface OnQuantityListener {
        void onQuantityChanged(int quantity);
    }


    public QuantityView(Context context) {
        super(context);
        init(null);
    }

    public QuantityView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public QuantityView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }


    private void init(AttributeSet attrs) {
        inflate(getContext(), R.layout.view_quantity, this);
        ButterKnife.bind(this);
        setSaveEnabled(true);

//        mPictureView = (ImageView) findViewById(R.id.view_take_picture_imageview);
//        mPictureActionsContainer = findViewById(R.id.view_take_picture_actions_container);
//        mPictureDeleteAction = findViewById(R.id.view_take_picture_delete_action);
//
//        mPictureActionsContainer.setVisibility(View.GONE);
    }

    public void setOnQuantityListener(OnQuantityListener listener) {
        mQuantityListener = listener;
    }

    public int getQuantity() {
        return Integer.parseInt(mQuantity.getText().toString());
    }

    public void setQuantity(int quantity) {
        mQuantity.setText(String.valueOf(quantity));
        mQuantityRemove.setVisibility(quantity > 0 ? View.VISIBLE : View.INVISIBLE);
        if (mQuantityListener != null) {
            mQuantityListener.onQuantityChanged(quantity);
        }
    }

    @OnClick(R.id.quantity_vw_quantity_add_floatingactionbutton)
    public void addQuantity() {
        setQuantity(getQuantity() + 1);
    }

    @OnClick(R.id.quantity_vw_quantity_remove_floatingactionbutton)
    public void removeQuantity() {
        setQuantity(getQuantity() - 1);
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.quantity = getQuantity();
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        int quantity = ss.quantity;
        mQuantity.setText(String.valueOf(quantity));
        mQuantityRemove.setVisibility(quantity > 0 ? View.VISIBLE : View.INVISIBLE);
    }

    static class SavedState extends BaseSavedState {
        int quantity;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            quantity = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(quantity);
        }

        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

    }
}
