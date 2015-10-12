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

import static java.lang.Math.max;

/**
 * Binary tree.
 *
 * A simple binary tree for different purposes.
 *
 * @author Saúl Vargas (Saul.Vargas@glasgow.ac.uk)
 */
public class BinaryTree {

    private final Node rootNode;

    public BinaryTree(Node root) {
        rootNode = root;
    }

    public int numNodes() {
        return rootNode.numNodes();
    }

    public int depth() {
        return rootNode.depth();
    }

    public Node getRoot() {
        return rootNode;
    }

    public static abstract class Node {

        private Node p;
        private Node l;
        private Node r;

        public Node() {
            p = null;
            l = null;
            r = null;
        }

        public Node getParent() {
            return p;
        }

        public Node getLeftChild() {
            return l;
        }

        public void setLeftChild(Node leftChild) {
            this.l = leftChild;
            if (leftChild != null) {
                this.l.p = this;
            }
        }

        public Node getRightChild() {
            return r;
        }

        public void setRightChild(Node rightChild) {
            this.r = rightChild;
            if (rightChild != null) {
                this.r.p = this;
            }
        }

        public boolean isLeaf() {
            return l == null && r == null;
        }

        public int numNodes() {
            int c = 1;
            if (this.l != null) {
                c += l.numNodes();
            }
            if (this.r != null) {
                c += r.numNodes();
            }

            return c;
        }

        public int depth() {
            if (isLeaf()) {
                return 1;
            } else {
                return 1 + max(l.depth(), r.depth());
            }
        }

    }
}
