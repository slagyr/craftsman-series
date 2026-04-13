public enum RGB {
  red(0xff, 0x00, 0x00),
  green(0x00, 0xff, 0x00),
  blue(0x00, 0x00, 0xff),
  white(0xff,0xff,0xff);

  public final int r;
  public final int g;
  public final int b;

  RGB(int r, int g, int b) {
    this.r = r;
    this.g = g;
    this.b = b;
  }

    public boolean isBlue(){
    return b > 2*(r + g);
  }
}

