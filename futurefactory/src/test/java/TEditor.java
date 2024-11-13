import javax.swing.JFrame;

import com.futurefactory.Data.Editable;
import com.futurefactory.IEditor;
import com.futurefactory.Root;

public class TEditor implements IEditor{
    public void constructEditor(Editable m){
        JFrame f=new JFrame();
        f.setSize(Root.SCREEN_SIZE);
        f.setVisible(true);
    }
}
