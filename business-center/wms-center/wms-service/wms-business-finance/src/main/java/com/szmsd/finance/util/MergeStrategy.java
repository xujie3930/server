package com.szmsd.finance.util;

import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.List;

public class MergeStrategy implements CellWriteHandler {

    private int[] mergeColumnIndex;

    private int mergeRowIndex;

    public MergeStrategy(int[] mergeColumnIndex,int mergeRowIndex){
        this.mergeColumnIndex = mergeColumnIndex;
        this.mergeRowIndex = mergeRowIndex;
    }


    @Override
    public void beforeCellCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Row row, Head head, Integer columnIndex, Integer relativeRowIndex, Boolean isHead) {

    }

    @Override
    public void afterCellCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {

    }

    @Override
    public void afterCellDataConverted(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, CellData cellData, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {

    }

    @Override
    public void afterCellDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, List<CellData> cellDataList, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {

        int curRowIndex = cell.getRowIndex();
        int curColumnIndex = cell.getColumnIndex();

        if(curRowIndex > mergeRowIndex){
            for(int columnIndex : mergeColumnIndex){

                if(columnIndex == curColumnIndex){
                    mergeWithPreRow(writeSheetHolder,cell,curRowIndex,curColumnIndex);
                    break;
                }

            }
        }
    }

    private void mergeWithPreRow(WriteSheetHolder writeSheetHolder, Cell cell, int curRowIndex, int curColumnIndex) {

        Object curData = cell.getCellTypeEnum() == CellType.STRING ? cell.getStringCellValue() : cell.getNumericCellValue();
        Cell preCell = cell.getSheet().getRow(curRowIndex -1 ).getCell(curColumnIndex);
        Object preData = preCell.getCellTypeEnum() == CellType.STRING ? preCell.getStringCellValue():preCell.getNumericCellValue();

        if(curData.equals(preData)){
            Sheet sheet = writeSheetHolder.getSheet();
            List<CellRangeAddress> mergedRegions = sheet.getMergedRegions();

            boolean isMerged = false;

            for(int i = 0;i<mergedRegions.size() && !isMerged;i++){
                CellRangeAddress cellRangeAddress = mergedRegions.get(i);
                if(cellRangeAddress.isInRange(curRowIndex - 1, curColumnIndex)){

                    sheet.removeMergedRegion(i);
                    cellRangeAddress.setLastRow(curRowIndex);
                    sheet.addMergedRegion(cellRangeAddress);
                    isMerged = true;
                }
            }

            if(!isMerged){
                CellRangeAddress cellRangeAddress = new CellRangeAddress(curRowIndex -1,curRowIndex,curColumnIndex,curColumnIndex);
                sheet.addMergedRegion(cellRangeAddress);
            }

        }

    }
}
