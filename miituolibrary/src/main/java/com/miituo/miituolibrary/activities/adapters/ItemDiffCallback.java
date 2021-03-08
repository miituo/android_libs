package com.miituo.miituolibrary.activities.adapters;

import androidx.recyclerview.widget.DiffUtil;

import com.miituo.miituolibrary.activities.data.InfoClient;

import java.util.List;

public class ItemDiffCallback extends DiffUtil.Callback{

    //private final List<Employee> mOldEmployeeList;
    private final List<InfoClient> mInfoClientList;
    private final List<InfoClient> mInfoClientListNew;

    public ItemDiffCallback(List<InfoClient> mInfoClientList, List<InfoClient> mInfoClientListNew) {
        this.mInfoClientList = mInfoClientList;
        this.mInfoClientListNew = mInfoClientListNew;
    }

    @Override
    public int getOldListSize() {
        return mInfoClientList.size();
    }

    @Override
    public int getNewListSize() {
        return mInfoClientListNew.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        //return false;
        return mInfoClientList.get(oldItemPosition).getPolicies().getId() == mInfoClientListNew.get(
                newItemPosition).getPolicies().getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        //return false;
        final InfoClient oldEmployee = mInfoClientList.get(oldItemPosition);
        final InfoClient newEmployee = mInfoClientListNew.get(newItemPosition);

        return oldEmployee.getClient().getName().equals(newEmployee.getClient().getName());
    }
}
