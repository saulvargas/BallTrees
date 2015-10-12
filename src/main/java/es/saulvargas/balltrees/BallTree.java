/* 
 * Copyright (C) 2015 Saúl Vargas http://saulvargas.es
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.saulvargas.balltrees;

import es.saulvargas.balltrees.BinaryTree.Node;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import static java.lang.Math.max;
import static java.lang.Math.sqrt;
import java.util.Random;

/**
 * Ball tree.
 *
 * @author Saúl Vargas (Saul.Vargas@glasgow.ac.uk)
 */
public class BallTree extends BinaryTree {

    private static final Random random = new Random();

    private BallTree(Node root) {
        super(root);
    }

    @Override
    public Ball getRoot() {
        return (Ball) super.getRoot();
    }

    public static BallTree create(double[][] itemMatrix, int leafThreshold, int maxDepth) {
        int[] rows = new int[itemMatrix.length];
        for (int row = 0; row < itemMatrix.length; row++) {
            rows[row] = row;
        }

        Ball root = new Ball(rows, itemMatrix);
        BallTree tree = new BallTree(root);

        int depth = 0;
        if (rows.length > leafThreshold && depth < maxDepth) {
            createChildren(root, leafThreshold, depth + 1, maxDepth);
        }

        return tree;
    }

    private static void createChildren(Ball parent, int leafThreshold, int depth, int maxDepth) {
        IntArrayList leftRows = new IntArrayList();
        IntArrayList rightRows = new IntArrayList();

        splitItems(parent.getRows(), parent.getItemMatrix(), leftRows, rightRows);
        parent.clearRows();

        Ball leftChild = new Ball(leftRows.toIntArray(), parent.getItemMatrix());
        parent.setLeftChild(leftChild);
        if (leftChild.getRows().length > leafThreshold && depth < maxDepth) {
            createChildren(leftChild, leafThreshold, depth + 1, maxDepth);
        }

        Ball rightChild = new Ball(rightRows.toIntArray(), parent.getItemMatrix());
        parent.setRightChild(rightChild);
        if (rightChild.getRows().length > leafThreshold) {
            createChildren(rightChild, leafThreshold, depth + 1, maxDepth);
        }
    }

    protected static void splitItems(int[] rows, double[][] itemMatrix, IntArrayList leftRows, IntArrayList rightRows) {
        // pick random element
        double[] x = itemMatrix[rows[random.nextInt(rows.length)]];
        // select furthest point A to x
        double[] A = x;
        double dist1 = 0;
        for (int row : rows) {
            double[] y = itemMatrix[row];
            double dist2 = distance2(x, y);
            if (dist2 > dist1) {
                A = y;
                dist1 = dist2;
            }
        }
        // select furthest point B to A
        double[] B = A;
        dist1 = 0;
        for (int row : rows) {
            double[] y = itemMatrix[row];
            double dist2 = distance2(A, y);
            if (dist2 > dist1) {
                B = y;
                dist1 = dist2;
            }
        }

        // split data according to A and B proximity
        for (int row : rows) {
            double[] y = itemMatrix[row];
            double distA = distance2(A, y);
            double distB = distance2(B, y);

            if (distA <= distB) {
                leftRows.add(row);
            } else {
                rightRows.add(row);
            }
        }
    }

    public static class Ball extends Node {

        private double[] center;
        private double radius;
        private int[] rows;
        private final double[][] itemMatrix;

        public Ball(int[] rows, double[][] itemMatrix) {
            this.rows = rows;
            this.itemMatrix = itemMatrix;
            calculateCenter();
            calculateRadius();
        }

        @Override
        public Ball getParent() {
            return (Ball) super.getParent();
        }

        @Override
        public Ball getLeftChild() {
            return (Ball) super.getLeftChild();
        }

        @Override
        public Ball getRightChild() {
            return (Ball) super.getRightChild();
        }

        private void calculateCenter() {
            center = new double[itemMatrix[0].length];

            for (int row : rows) {
                for (int i = 0; i < center.length; i++) {
                    center[i] += itemMatrix[row][i];
                }
            }
            for (int i = 0; i < center.length; i++) {
                center[i] /= rows.length;
            }
        }

        private void calculateRadius() {
            radius = Double.NEGATIVE_INFINITY;

            for (int row : rows) {
                radius = max(radius, distance2(center, itemMatrix[row]));
            }
            radius = sqrt(radius);
        }

        public double mip(double[] q) {
            return dotProduct(q, center) + radius * norm(q);
        }

        public double mip(Ball ball) {
            double[] p0 = center;
            double[] q0 = ball.getCenter();
            double rp = radius;
            double rq = ball.getRadius();
            return dotProduct(p0, q0) + rp * rq + rq * norm(p0) + rp * norm(q0);
        }

        public double[] getCenter() {
            return center;
        }

        public double getRadius() {
            return radius;
        }

        public int[] getRows() {
            return rows;
        }

        public void clearRows() {
            rows = null;
        }

        public double[][] getItemMatrix() {
            return itemMatrix;
        }

    }

    public static double distance(double[] x, double[] y) {
        return sqrt(distance2(x, y));
    }

    public static double distance2(double[] x, double[] y) {
        double d = 0.0;
        for (int i = 0; i < x.length; i++) {
            d += (x[i] - y[i]) * (x[i] - y[i]);
        }
        
        return d;
    }

    public static double norm(double[] x) {
        return sqrt(norm2(x));
    }

    public static double norm2(double[] x) {
        return dotProduct(x, x);
    }

    public static double dotProduct(double[] x, double[] y) {
        double p = 0.0;
        for (int i = 0; i < x.length; i++) {
            p += x[i] * y[i];
        }

        return p;
    }
}
