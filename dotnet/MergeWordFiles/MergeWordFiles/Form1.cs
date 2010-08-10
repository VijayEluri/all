using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.IO;
using System.Text;
using System.Windows.Forms;
using Word = Microsoft.Office.Interop.Word;

namespace MergeWordFiles
{
    public partial class Form1 : Form
    {
        private Word.Application wordApp;

        public Form1()
        {
            InitializeComponent();
            wordApp = new Word.Application();
        }

        private void appendToListView(String fileName)
        {
            for (int i = 0; i < listView1.Items.Count; ++i) {
                String itemFile = listView1.Items[i].SubItems[1].Text;
                if (itemFile.ToUpper().CompareTo(fileName.ToUpper()) == 0) {
                    return;
                }
            }
            int index = listView1.Items.Count + 1;
            ListViewItem item = listView1.Items.Add(index.ToString());
            item.SubItems.Add(fileName);
        }

        private void listView1_DragDrop(object sender, DragEventArgs e)
        {
            Array files = (Array)e.Data.GetData(DataFormats.FileDrop);
            if (files != null) {
                for (int i = 0; i < files.Length; ++i) {
                    String fileName = files.GetValue(i).ToString();
                    appendToListView(fileName);
                }
            }
        }

        private void listView1_DragEnter(object sender, DragEventArgs e)
        {
            if (e.Data.GetDataPresent(DataFormats.FileDrop)) {
                e.Effect = DragDropEffects.Copy;
            } else {
                e.Effect = DragDropEffects.None;
            }
        }

        private void button2_Click(object sender, EventArgs e)
        {
            Close();
        }

        private Word.Document OpenDoc(String fileName)
        {
            Object oFilename = fileName;
            Object oNull = System.Reflection.Missing.Value;
            return wordApp.Documents.Open(
                ref oFilename,
                ref oNull,
                ref oNull,
                ref oNull,
                ref oNull,
                ref oNull,
                ref oNull,
                ref oNull,
                ref oNull,
                ref oNull,
                ref oNull,
                ref oNull,
                ref oNull,
                ref oNull,
                ref oNull,
                ref oNull);
        }

        private void button1_Click(object sender, EventArgs e)
        {
            int itemCount = listView1.Items.Count;

            if (itemCount < 2) {
                MessageBox.Show("Plz drag&drop more than 2 word files to start.");
                return;
            }

            String firstFile = listView1.Items[0].SubItems[1].Text;
            String targetFile = Path.Combine(Path.GetTempPath(), Path.GetFileName(firstFile));
            File.Copy(firstFile, targetFile, true);

            Object oFalse = false;  
            Object oNull = System.Reflection.Missing.Value;

            Word.Document targetDoc = OpenDoc(targetFile);
            for (int i = 1; i < itemCount; ++i) {
                String fileName = listView1.Items[i].SubItems[1].Text;
                Word.Document anotherDoc = OpenDoc(fileName);
                wordApp.MergeDocuments(targetDoc, anotherDoc,
                    Word.WdCompareDestination.wdCompareDestinationOriginal, Word.WdGranularity.wdGranularityCharLevel,
                    false, true, true, true, true, true, true, true, true, true, "", "", Word.WdMergeFormatFrom.wdMergeFormatFromOriginal);
                ((Word._Document) anotherDoc).Close(ref oNull, ref oNull, ref oNull);
                anotherDoc = null;
            }

            targetDoc.Save();
            ((Word._Document)targetDoc).Close(ref oNull, ref oNull, ref oNull);
            targetDoc = null;

            System.Diagnostics.Process.Start(targetFile);
        }

        private void Form1_FormClosed(object sender, FormClosedEventArgs e)
        {
            wordApp = null;
        }
    }
}
