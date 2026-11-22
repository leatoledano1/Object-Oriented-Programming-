public class RendererFactory {
    RendererFactory(){}

    // renderer factory using the console renderer from moodle and
    //void renderer that does not show the board
    public Renderer buildRenderer(String type, int size){
        String lowType = type.toLowerCase();

        switch (lowType){
            case "console":
                return new ConsoleRenderer(size);
            case "void":
                return new VoidRenderer();
            default:
                return null;
        }
    }
}
