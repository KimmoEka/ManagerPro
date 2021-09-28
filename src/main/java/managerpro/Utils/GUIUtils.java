package managerpro.Utils;

import javafx.scene.control.TableView;
import javafx.scene.text.Text;

/**
 * Utility class to automatically resize TableView columns
 */
public class GUIUtils {

    /*
    Copied from https://stackoverflow.com/questions/14650787/javafx-column-in-tableview-auto-fit-size
     */
    public static void autoResizeColumns( TableView<?> table )
    {
        //Set the right policy
        table.setColumnResizePolicy( TableView.UNCONSTRAINED_RESIZE_POLICY);
        table.getColumns().stream().forEach( (column) ->
        {
            //Minimal width = columnheader
            Text t = new Text( column.getText() );
            double max = t.getLayoutBounds().getWidth();
            for ( int i = 0; i < table.getItems().size(); i++ )
            {
                //cell must not be empty
                if ( column.getCellData( i ) != null )
                {
                    t = new Text( column.getCellData( i ).toString() );
                    double calcwidth = t.getLayoutBounds().getWidth();
                    //remember new max-width
                    if ( calcwidth > max )
                    {
                        max = calcwidth;
                    }
                }
            }

            //set the new max-widht with some extra space
            column.setPrefWidth( max + 10.0d );
        } );
    }
}