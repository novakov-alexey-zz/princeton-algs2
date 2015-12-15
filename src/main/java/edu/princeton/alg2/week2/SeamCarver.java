package edu.princeton.alg2.week2;

import edu.princeton.cs.algs4.Picture;

import java.awt.Color;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * @author Alexey Novakov
 */
public class SeamCarver {
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
        BiFunction<Color, Color, Double> diff = (l, r) ->
                Math.pow(r.getRed() - l.getRed(), 2)
                        + Math.pow(r.getGreen() - l.getGreen(), 2)
                        + Math.pow(r.getBlue() - l.getBlue(), 2);

        double xRgb = diff.apply(picture.get(i + 1, j), picture.get(i - 1, j));
        double yRgb = diff.apply(picture.get(i, j + 1), picture.get(i, j - 1));

        return Math.sqrt(xRgb + yRgb);
    }

    private boolean isBorder(int i, int j) {
        return i == 0 || j == 0 || i == picture.width() - 1 || j == picture.height() - 1;
    }

    // current picture
    public Picture picture() {
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
        return !isBorder(x, y) ? getEnergy(x, y) : 1_000;
    }

    private void validateIndexInbound(int x, int y) {
        if (x < 0 || x > width() - 1 || y < 0 || y > height() - 1)
            throw new IndexOutOfBoundsException(String.format("Either x or y is out of bound: w = %d, h = %d", width(), height()));
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        rotateIfNeeded();
        return findVerticalSeam();
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        return null;//TODO
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
            rotateMatrix(colors);
        }
    }

    private <T> void rotateMatrix(T[][] matrix) {
        int n = matrix.length;
        for (int i = 0; i < n / 2; i++) {
            for (int j = 0; j < Math.ceil(((double) n) / 2.); j++) {
                T temp = matrix[i][j];
                matrix[i][j] = matrix[n - 1 - j][i];
                matrix[n - 1 - j][i] = matrix[n - 1 - i][n - 1 - j];
                matrix[n - 1 - i][n - 1 - j] = matrix[j][n - 1 - i];
                matrix[j][n - 1 - i] = temp;
            }
        }
        rotated = !rotated;
    }
}
