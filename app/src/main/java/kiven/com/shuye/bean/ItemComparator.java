package kiven.com.shuye.bean;

import java.util.Comparator;

/**
 * Author:          Kevin <BR/>
 * CreatedTime:     2018/1/22 20:22 <BR/>
 * Desc:            TODO <BR/>
 * <p/>
 * ModifyTime:      <BR/>
 * ModifyItems:     <BR/>
 */
public class ItemComparator implements Comparator<Item> {

    @Override
    public int compare(Item o1, Item o2) {
        return (int) (o1.getTime() - o2.getTime());
    }
}
