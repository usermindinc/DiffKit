/**
 * Copyright 2010 Joseph Panico
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.diffkit.db.tst


import java.io.File;

import org.diffkit.db.DKDBColumn 
import org.diffkit.db.DKDBConnectionInfo 
import org.diffkit.db.DKDBDatabase 
import org.diffkit.db.DKDBFlavor 
import org.diffkit.db.DKDBInsertTableLoader;
import org.diffkit.db.DKDBPrimaryKey 
import org.diffkit.db.DKDBTable;
import org.diffkit.util.DKResourceUtil 

import groovy.util.GroovyTestCase;


/**
 * @author jpanico
 */
public class TestInsertTableLoader extends GroovyTestCase {
   
   public void testLoader(){
      DKDBConnectionInfo connectionInfo = ['test', DKDBFlavor.H2,"mem:test", null, null, 'test', 'test']
      println "connectionInfo->$connectionInfo"
      DKDBDatabase database = [connectionInfo]
      def metaTable = this.createCustomerMetaTable()
      assert metaTable
      if(database.tableExists(metaTable))
         database.dropTable(metaTable)
      database.createTable(metaTable)
      
      def csvFile = this.getCsvFile()
      def loader = new DKDBInsertTableLoader(database)
      
      assert loader.load( metaTable, csvFile)
      
      def rows = database.readAllRows(metaTable)
      assert rows
      println "rows->$rows"
      assert rows.size() ==2
      def rob = rows.find { it['FIRST_NAME'] == 'rob'}
      assert rob
      assert rob['LAST_NAME'] == 'smith'
      assert rob['AGE'] == 50
      
      database.dropTable(metaTable)
   }
   
   private File getCsvFile(){
      def csvFile = DKResourceUtil.findResourceAsFile('org/diffkit/db/tst/customers.csv')
      println "csvFile->$csvFile"
      assert csvFile
      return csvFile
   }
   
   private DKDBTable createCustomerMetaTable(){
      DKDBColumn column1 = ['first_name', 1, 'VARCHAR', 50, true]
      DKDBColumn column2 = ['last_name', 2, 'VARCHAR', 50, true]
      DKDBColumn column3 = ['address', 3, 'VARCHAR', 50, true]
      DKDBColumn column4 = ['city', 4, 'VARCHAR', 50, true]
      DKDBColumn column5 = ['country', 5, 'VARCHAR', 25, true]
      DKDBColumn column6 = ['age', 6, 'INTEGER', -1, true]
      DKDBColumn[] columns = [column1, column2, column3, column4, column5, column6]
      String[] pkColNames = ['first_name', 'last_name']
      DKDBPrimaryKey pk = ['pk_customer', pkColNames]
      DKDBTable table = [ null, null, 'CUSTOMER', columns, pk]
      return table
   }
}