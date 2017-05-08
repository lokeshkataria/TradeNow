package tradenow.com.saxoopenapiapp;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by Lokesh on 04-05-2017.
 */

public class InstrumentAdapter extends ArrayAdapter {

    private FxSpotInstrument fxSpotInstrument[];

    public void setFxSpotInstrument(FxSpotInstrument[] fxSpotInstrument) {
        this.fxSpotInstrument = fxSpotInstrument;
    }

    public InstrumentAdapter(@NonNull Context context, @LayoutRes int resource, FxSpotInstrument[] instruments) {
        super(context, resource);
        fxSpotInstrument = instruments;
    }

    @Override
    public int getCount() {
        return fxSpotInstrument.length;
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return fxSpotInstrument[position];
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.card_instrument,parent,false);
        }
        FxSpotInstrument fxSpotInstrument = (FxSpotInstrument) getItem(position);
        TextView description = (TextView) convertView.findViewById(R.id.instrument_row_description);
        TextView symbol = (TextView) convertView.findViewById(R.id.instrument_row_symbol);
        TextView price = (TextView) convertView.findViewById(R.id.instrument_row_price);
        description.setText(fxSpotInstrument.getDescription());
        symbol.setText(fxSpotInstrument.getSymbol());
        price.setText(fxSpotInstrument.getPrice());
        convertView.setTag(fxSpotInstrument.getIdentifier());
        return convertView;
    }
}
