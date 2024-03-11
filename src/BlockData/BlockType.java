package BlockData;

public enum BlockType {
    DIRT{
        @Override
        public String getTexturePath() {
            return texturePath;
        }

        @Override
        public boolean isTransparent() {
            return false;
        }
    },
    GLASS {
        @Override
        public String getTexturePath() {
            return texturePath;
        }

        @Override
        public boolean isTransparent() {
            return true;
        }
    };

    final String texturePath = "resources/textures/" + this.name().toLowerCase() + ".png";

    public abstract String getTexturePath();
    public abstract boolean isTransparent();
}
