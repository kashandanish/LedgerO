package com.ledgero.Repositories

import androidx.lifecycle.LiveData
import com.ledgero.DAOs.IndividualScreenDAO
import com.ledgero.DAOs.UnApproveEntriesDAO
import com.ledgero.DataClasses.Entries

class UnApprovedEntriesRepo(private val unApproveEntriesDAO: UnApproveEntriesDAO)  {

    fun getEntries(): LiveData<ArrayList<Entries>> {

        return unApproveEntriesDAO.getUnApprovedEntries()

    }

    fun removeListener(){
       unApproveEntriesDAO.removeListener()
    }

    fun rejectNewAddedEntry(pos: Int){
        unApproveEntriesDAO.enteryRejected(pos)
    }

    fun rejectDeleteRequestForApprovedEntry(pos:Int){
        unApproveEntriesDAO.rejectDeleteRequestForApprovedEntry(pos)
    }



    fun deleteEntry(pos: Int){
        unApproveEntriesDAO.deleteEntry_Approved(pos)
    }
    fun approveEntry(pos:Int){
        unApproveEntriesDAO.entryApprove(pos)
    }
    fun rejectedEntry(pos:Int){
        unApproveEntriesDAO.enteryRejected(pos)
    }

    fun deleteUnApprovedEntryThenUpdateLedgerEntry(pos: Int) {
unApproveEntriesDAO.deleteUnApprovedEntryThenUpdateLedgerEntry(pos)
    }

}