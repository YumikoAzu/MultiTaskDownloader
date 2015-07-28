package com.yumikoazu.multitaskdownloader.download;

/**
 * Created by joker on 2015/7/27.
 */
public class DynamicArray<T> {
    private T[] elems;
    private int mRight; // 右侧有内容索引值，即队列尾

    private int mLeft; // 左侧有内容索引值，即队列首

    private int INCREATE_STEP = 12;

    public DynamicArray() {
        elems = (T[]) new Object[INCREATE_STEP];
        mLeft = 0;
        mRight = 0;
    }

    /**
     * 插入一个元素到数组
     *
     * @param t
     */
    public void insert(T t) {
        // 扩展数组
        if (mRight >= elems.length) {
            T[] temp = (T[]) new Object[elems.length + INCREATE_STEP];
            for (int i = 0; i < elems.length; i++) {
                temp[i] = elems[i];
            }
            elems = temp;
            temp = null;
        }

        if (elems[mRight] == null) {
            elems[mRight++] = t;
        } else {
            elems[mRight++] = t;
        }
    }

    public T peek() {
        if (!isEmpty()) {
            return elems[mLeft];
        }
        return null;
    }

    /**
     * 弹出一个元素，将数组起点到p之间的元素都往右移动一位
     *
     * @return
     */
    public T poll() {
        if (mLeft == mRight) {
            System.out.println("数组为空，无法移除");
            return null;
        } else {
            T t = elems[mLeft];
            elems[mLeft++] = null;
            return t;
        }
    }

    /**
     * 删除mLeft和mRight之间的元素，从0开始
     *
     * @param p
     */
    public void delete(int p) {
        p = p + mLeft;
        if (p >= mRight) {
            System.out.println("无效的索引值,无法进行删除");
        } else {
            for (int i = p; i > mLeft; i--) {
                elems[i] = elems[i - 1];
            }
            elems[mLeft] = null;
        }
        mLeft++;
    }

    /**
     * 返回数组实际保存的有效个数
     *
     * @return
     */
    public int size() {

        return (mRight - mLeft);
    }

    /**
     * 得到mLeft和mRight之间第p个元素，从0开始
     *
     * @param p
     * @return
     */
    public T getObjectAt(int p) {
        p = p + mLeft;
        if (p >= mRight) {
            System.out.println("无效的索引值,无法进行查找");
            return null;
        } else {
            return elems[p];
        }
    }

    /**
     * 数组是否为空
     *
     * @return
     */
    public boolean isEmpty() {
        return (mRight <= mLeft);
    }

    public boolean contains(Object object) {
        Object[] a = elems;
        int s = size();
        if (object != null) {
            for (int i = 0; i < s; i++) {
                if (object.equals(a[i])) {
                    return true;
                }
            }
        } else {
            for (int i = 0; i < s; i++) {
                if (a[i] == null) {
                    return true;
                }
            }
        }
        return false;
    }


}
