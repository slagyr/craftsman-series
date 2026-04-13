public class Taco {
  enum Color {
    red, green, burntumber}

  enum Salsa {
    mild(10, Color.red) {
      public int sips() {
        return 0;
      }
    },
    medium(1000, Color.green){
      public int sips() {
        return 1;
      }
    },
    hot(100000, Color.burntumber){
      public int sips() {
        return 5;
      }
    };

    public final int scovilles;
    public final Color color;
    public abstract int sips();

    Salsa(int scovilles, Color color) {
      this.scovilles = scovilles;
      this.color = color;
    }

    public boolean isTooHotForMe() {
      return scovilles > 30000;
    }
  }
}