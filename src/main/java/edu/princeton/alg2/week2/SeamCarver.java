package edu.princeton.alg2.week2;

import edu.princeton.cs.algs4.Picture;

import java.awt.*;
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
            return 1_000;
        }
    }

    private boolean isBorder(int i, int j) {
        return i == 0 || j == 0 || i == colors.length - 1 || j == colors[0].length - 1;
    }

    // current picture
    public Picture picture() {
        rotateBackIfNeeded();

        Picture picture = new Picture(colors.length, colors[0].length);
        for (int i = 0; i < picture.width(); i++) {
            for (int j = 0; j < picture.height(); j++) {
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
        int[] seam = findSeam(false);
        int[] horizontalSeam = new int[seam.length];

        for (int i = 0; i < seam.length; i++) {
            horizontalSeam[i] = seam.length - 1 - horizontalSeam[i];
        }
        return horizontalSeam;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        return findSeam(true);
    }

    private int[] findSeam(boolean vertical) {
        if (!vertical)
            rotateIfNeeded();
        else
            rotateBackIfNeeded();

        int[] seam = new int[colors.length];
        int l = 0;
        int width = colors[0].length;
        int r = width;

        for (int i = 1; i < colors.length; i++) {
            double min = DEFAULT_ENERGY;
            for (int j = l; j < r; j++) {
                double energy = getEnergy(i, j);
                if (energy < min) {
                    min = energy;
                    seam[i] = j;
                }
            }
            l = seam[i] > 0 ? seam[i] - 1 : 0;
            r = seam[i] < width - 1 ? seam[i] + 1 : width;
        }

        seam[0] = seam[1] > 0 ? seam[1] - 1 : seam[1];
        return seam;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        Objects.requireNonNull(seam);
        rotateIfNeeded();
        removeVerticalSeam(seam);
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        Objects.requireNonNull(seam);
        //TODO
    }

    private void rotateIfNeeded() {
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

    public Color[][] rotateMatrixRight(Color[][] matrix) {
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


    public Color[][] rotateMatrixLeft(Color[][] matrix) {
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
