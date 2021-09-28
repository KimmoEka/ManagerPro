package managerpro.Model;

import java.util.List;

public interface ITablesDao {
    List<TablesEntity> getAllTables();
    TablesEntity getTable(int tableId);
    void createTable(TablesEntity table);
    void updateTable(TablesEntity table);
    void deleteTable(TablesEntity table);
}
