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
            return 1_000;
        }
    }

    private boolean isBorder(int i, int j) {
        return i == 0 || j == 0 || i == picture.width() - 1 || j == picture.height() - 1;
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
        rotateIfNeeded();
        return findVerticalSeam();
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        rotateBackIfNeeded();

        int[] seam = new int[colors[0].length];
        int l = 0;
        int r = colors[0].length;

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
            r = seam[i] < colors.length - 1 ? seam[i] + 1 : colors.length;
        }

        seam[0] = seam[1] > 0 ? seam[1] : 0;
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
            rotateByNinetyToRight(colors);
            rotated = true;
        }
    }

    private void rotateBackIfNeeded() {
        if (rotated) {
            rotateByNinetyToLeft(colors);
            rotated = false;
        }
    }

    private <T> void rotateByNinetyToLeft(T[][] m) {
        transpose(m);
        swapRows(m);
    }

    private <T> void rotateByNinetyToRight(T[][] m) {
        swapRows(m);
        transpose(m);
    }

    private static <T> void transpose(T[][] m) {
        for (int i = 0; i < m.length; i++) {
            for (int j = i; j < m[0].length; j++) {
                T x = m[i][j];
                m[i][j] = m[j][i];
                m[j][i] = x;
            }
        }
    }

    private static <T> void swapRows(T[][] m) {
        for (int i = 0, k = m.length - 1; i < k; ++i, --k) {
            T[] x = m[i];
            m[i] = m[k];
            m[k] = x;
        }
    }
}
