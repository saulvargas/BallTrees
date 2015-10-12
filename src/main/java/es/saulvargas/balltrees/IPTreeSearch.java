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

import es.saulvargas.balltrees.BallTree.Ball;
import static es.saulvargas.balltrees.BallTree.dotProduct;
import es.uam.eps.ir.ranksys.fast.utils.topn.IntDoubleTopN;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

/**
 * Inner product search with ball trees.
 *
 * @author Saúl Vargas (Saul.Vargas@glasgow.ac.uk)
 */
public class IPTreeSearch {

    public static IntDoubleTopN getTopNLinearSearch(double[][] itemMatrix, double[] q, int n) {
        return getTopNLinearSearch(itemMatrix, q, n, new IntOpenHashSet());
    }

    public static IntDoubleTopN getTopNLinearSearch(double[][] itemMatrix, double[] q, int n, IntSet excluded) {
        IntDoubleTopN topN = new IntDoubleTopN(n);

        for (int row = 0; row < itemMatrix.length; row++) {
            if (!excluded.contains(row)) {
                topN.add(row, dotProduct(q, itemMatrix[row]));
            }
        }

        return topN;
    }

    public static IntDoubleTopN getTopNSingleTree(BallTree ballTree, double[] q, int n) {
        return getTopNSingleTree(ballTree, q, n, new IntOpenHashSet());
    }

    public static IntDoubleTopN getTopNSingleTree(BallTree ballTree, double[] q, int n, IntSet excluded) {
        IntDoubleTopN topN = new IntDoubleTopN(n);

        Ball root = ballTree.getRoot();
        bbSingleTreeSearch(root, q, topN, excluded);

        return topN;
    }

    private static void bbSingleTreeSearch(Ball parent, double[] q, IntDoubleTopN topN, IntSet excluded) {
        if (!topN.isEmpty() && topN.peek().getDoubleValue() > parent.mip(q)) {
            return;
        }

        if (parent.isLeaf()) {
            linearSearch(parent, q, topN, excluded);
        } else {
            double mipL = parent.getLeftChild().mip(q);
            double minR = parent.getRightChild().mip(q);
            if (mipL < minR) {
                bbSingleTreeSearch(parent.getRightChild(), q, topN, excluded);
                bbSingleTreeSearch(parent.getLeftChild(), q, topN, excluded);
            } else {
                bbSingleTreeSearch(parent.getLeftChild(), q, topN, excluded);
                bbSingleTreeSearch(parent.getRightChild(), q, topN, excluded);
            }
        }
    }

    private static void linearSearch(Ball ball, double[] q, IntDoubleTopN topN, IntSet excluded) {
        for (int row : ball.getRows()) {
            if (!excluded.contains(row)) {
                topN.add(row, dotProduct(q, ball.getItemMatrix()[row]));
            }
        }
    }
}
