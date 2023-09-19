package Application;


import Models.CollectionManager;

public class Main {
    private static final String TEST_FILENAME = "test.json";
    static CollectionManager manager;
    static InputReader reader;

    public static void main(String[] args) {
        try {
//            manager = new CollectionManager(TEST_FILENAME);
//            reader = new InputReader(manager, System.in, true);
//            SpaceMarine marine = reader.GetSpaceMarine();
//            System.out.println(marine);
//            manager.add(marine);
//            manager.show();
            //CommandReader commandReader = new CommandReader(manager,System.in);
            //commandReader.Start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
//        CollectionManager_OLD collectionManagerOLD = new CollectionManager_OLD(args[0]);
//        CommandReader commandReader = new CommandReader();
//        commandReader.work(collectionManagerOLD);
    }

}