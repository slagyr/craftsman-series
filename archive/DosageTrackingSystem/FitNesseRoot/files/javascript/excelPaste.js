
function removeCarriageReturns(str)
{
	newStr = "";
	parts = str.split("\r");
	for(i = 0; i < parts.length; i++)
	{
		newStr += parts[i];
	}
	return newStr;
}

function translateExcelTableToFitnesseTable(excelTable)
{
	excelTable = removeCarriageReturns(excelTable);
	rowArray = excelTable.split("\n");
	fitnesseTable = "\n!|";
	for(i = 0; i < rowArray.length - 1; i++)
	{
		excelRowCells = rowArray[i].split("\t");
		for(j in excelRowCells)
		{
			fitnesseTable += excelRowCells[j];
			if(i == 0 && j == 0)
				fitnesseTable += "|";
			else
				fitnesseTable += "|";
		}
		fitnesseTable += "\n|";
	}
	return fitnesseTable.substring(0, fitnesseTable.length - 1);
}

function pasteExcelTable()
{
	clipboardValue = window.clipboardData.getData("Text");
	if(clipboardValue == null)
		return;
	document.f.pageContent.value += translateExcelTableToFitnesseTable(clipboardValue);
}

function appendTableFromExcel()
{
	var tableWindow = window.open("/files/pages/pasteTable.html", "TableWindow", 'scrollbars=auto,width=450,height=425,resizable=yes');
	tableWindow.creator = self;
}

if(browser_ie5up)
{
	document.write("<input type=\"button\" value=\"Paste Table From Excel\" onClick=\"pasteExcelTable()\">");
}
else
{
	document.write("<input type=\"button\" value=\"Paste Table From Excel\" onClick=\"appendTableFromExcel()\">");
}