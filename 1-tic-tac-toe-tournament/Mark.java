public enum Mark {
    BLANK, O, X;

    @Override
    public String toString() {
        if(this == BLANK) {
            return null;
        } else if(this == O) {
            return "O";
        } else if(this == X) {
            return "X";
        }
        return null;
    }
}

