package edu.princeton.alg2.week2;

import edu.princeton.cs.algs4.Picture;

import java.awt.Color;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * @author Alexey Novakov
 */
public class SeamCarver {
    public static final int DEFAULT_ENERGY = 1_000;
    private Picture picture;
    private Color[][] colors;
    private boolean rotated;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        this.picture = picture;
        setColors();
    }

    private void setColors() {
        colors = new Color[picture.width()][picture.height()];

        for (int i = 0; i < picture.width(); i++) {
            for (int j = 0; j < picture.height(); j++) {
                colors[i][j] = picture.get(i, j);
            }
        }
    }

    private double getEnergy(int i, int j) {
        if (!isBorder(i, j)) {
            BiFunction<Color, Color, Double> diff = (l, r) ->
                    Math.pow(r.getRed() - l.getRed(), 2)
                            + Math.pow(r.getGreen() - l.getGreen(), 2)
                            + Math.pow(r.getBlue() - l.getBlue(), 2);

            double xRgb = diff.apply(colors[i + 1][j], colors[i - 1][j]);
            double yRgb = diff.apply(colors[i][j + 1], colors[i][j - 1]);

            return Math.sqrt(xRgb + yRgb);
        } else {
            return DEFAULT_ENERGY;
        }
    }

    private boolean isBorder(int i, int j) {
        return i == 0 || j == 0 || i == colors.length - 1 || j == colors[0].length - 1;
    }

    // current picture
    public Picture picture() {
        rotateBackIfNeeded();

        picture = new Picture(colors.length, colors[0].length);
        for (int i = 0; i < colors.length; i++) {
            for (int j = 0; j < colors[0].length; j++) {
                picture.set(i, j, colors[i][j]);
            }
        }
        return picture;
    }

    // width of current picture
    public int width() {
        return picture.width();
    }

    // height of current picture
    public int height() {
        return picture.height();
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        validateIndexInbound(x, y);
        return getEnergy(x, y);
    }

    private void validateIndexInbound(int x, int y) {
        if (x < 0 || x >= width() || y < 0 || y >= height())
            throw new IndexOutOfBoundsException(String.format("Either x or y is out of bound: w = %d, h = %d", width(), height()));
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        return findSeam(false);
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        return findSeam(true);
    }

    private int[] findSeam(boolean vertical) {
        rotateIfNeeded(vertical);

        int[] seam = new int[colors[0].length];
        int left = 0;
        int width = colors.length;
        int right = width;

        for (int i = 1; i < seam.length; i++) {
            double min = Double.POSITIVE_INFINITY;
            for (int j = left; j < right; j++) {
                double energy = getEnergy(j, i);
                if (energy < min) {
                    min = energy;
                    seam[i] = j;
                }
            }
            left = seam[i] > 0 ? seam[i] - 1 : 0;
            right = seam[i] < width - 1 ? seam[i] + 1 : width;
        }

        if (seam.length > 1) {
            seam[0] = seam[1] > 0 ? seam[1] - 1 : seam[1];
        }
        return seam;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        throwExceptionIfPictureTooSmall(height(), "height");
        removeSeam(seam, false);
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        throwExceptionIfPictureTooSmall(width(), "width");
        removeSeam(seam, true);
    }

    private void throwExceptionIfPictureTooSmall(int length, String side) {
        if (length < 2) {
            throw new IllegalArgumentException(String.format("the %s of the picture is less than or equal to 1", side));
        }
    }

    private void removeSeam(int[] seam, boolean vertical) {
        Objects.requireNonNull(seam);
        throwExceptionIfWrongSeamLength(seam, vertical);

        rotateIfNeeded(vertical);
        Color[][] newColor = new Color[colors.length - 1][colors[0].length];

        int prevSeamEntry = seam[0];
        for (int j = 0; j < colors[0].length; j++) {
            if (Math.abs(prevSeamEntry - seam[j]) > 1) {
                throw new IllegalArgumentException("Current and previous seam entries differ by more than 1");
            }
            if (seam[j] < 0 || seam[j] >= colors[0].length) {
                throw new IllegalArgumentException(String.format("Seam entry %d is outside its prescribed range", seam[j]));
            }
            for (int i = 0, k = 0; i < colors.length; i++) {
                if (seam[j] != i) {
                    newColor[k++][j] = colors[i][j];
                }
            }
        }

        colors = newColor;
    }

    private void throwExceptionIfWrongSeamLength(int[] seam, boolean vertical) {
        int pictureLength = rotated ? colors[0].length : colors.length;

        if (vertical && seam.length < pictureLength) {
            throw new IllegalArgumentException(
                    String.format("wrong length of the seam. Seam length is %d, but picture height is %d", seam.length, pictureLength));

        } else if (!vertical && seam.length < pictureLength) {
            throw new IllegalArgumentException(
                    String.format("wrong length of the seam. Seam length is %d, but picture width is %d", seam.length, pictureLength));
        }
    }

    private void rotateIfNeeded(boolean vertical) {
        if (!vertical) {
            rotateClockwiseIfNeeded();
        } else {
            rotateBackIfNeeded();
        }
    }

    private void rotateClockwiseIfNeeded() {
        if (!rotated) {
            colors = rotateMatrixRight(colors);
            rotated = true;
        }
    }

    private void rotateBackIfNeeded() {
        if (rotated) {
            colors = rotateMatrixLeft(colors);
            rotated = false;
        }
    }

    private Color[][] rotateMatrixRight(Color[][] matrix) {
    /* W and H are already swapped */
        int w = matrix.length;
        int h = matrix[0].length;
        Color[][] ret = new Color[h][w];
        for (int i = 0; i < h; ++i) {
            for (int j = 0; j < w; ++j) {
                ret[i][j] = matrix[w - j - 1][i];
            }
        }
        return ret;
    }


    private Color[][] rotateMatrixLeft(Color[][] matrix) {
    /* W and H are already swapped */
        int w = matrix.length;
        int h = matrix[0].length;
        Color[][] ret = new Color[h][w];
        for (int i = 0; i < h; ++i) {
            for (int j = 0; j < w; ++j) {
                ret[i][j] = matrix[j][h - i - 1];
            }
        }
        return ret;
    }
}
