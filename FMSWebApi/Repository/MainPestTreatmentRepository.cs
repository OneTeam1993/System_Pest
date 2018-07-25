﻿using FMSWebApi.Models;
using MySql.Data.MySqlClient;
using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Web;

namespace FMSWebApi.Repository
{
    public class MainPestTreatmentRepository : IMainPestTreatmentRepository
    {
        //private string mConnStr = "server=127.0.0.1;uid=root;pwd=12345;database=test;";
        private string mConnStr = "server=localhost;uid=root;pwd=@c3c0M;database=sp;max pool size=500;";
        private string mProjName = "SP";
        public IEnumerable<MainPestTreatmentInfo> GetAll()
        {
            MySqlConnection conn = new MySqlConnection();
            MySqlCommand cmd = new MySqlCommand();
            List<MainPestTreatmentInfo> arrPests = new List<MainPestTreatmentInfo>();

            try
            {
                conn.ConnectionString = mConnStr;
                conn.Open();

                cmd.CommandText = "main_pests_treatment";
                cmd.Connection = conn;
                cmd.CommandType = CommandType.TableDirect;
                MySqlDataReader reader = cmd.ExecuteReader();

                //Console.WriteLine(String.Format("Total Data: {0}", dbRdr.FieldCount));

                if ((reader != null) && (reader.HasRows))
                {
                    while (reader.Read())
                    {
                        MainPestTreatmentInfo currPestTreatment = DataMgrTools.BuildMainPestTreatment(reader);
                        arrPests.Add(currPestTreatment);
                    }
                }
                conn.Close();
            }
            catch (MySqlException ex)
            {
                Logger.LogEvent(mProjName, ex.Message, System.Diagnostics.EventLogEntryType.Error);
            }

            if (conn != null)
                conn.Close();

            return arrPests.ToArray();
        }

        public MainPestTreatmentInfo Get(int pestID)
        {
            MainPestTreatmentInfo currPestTreatment = new MainPestTreatmentInfo();
            string query = string.Format("SELECT * FROM main_pests_treatment WHERE main_pest_treatment_id = {0}", pestID);

            using (MySqlConnection conn = new MySqlConnection(mConnStr))
            {
                try
                {
                    using (MySqlCommand cmd = new MySqlCommand(query, conn))
                    {
                        conn.Open();
                        MySqlDataReader reader = cmd.ExecuteReader();

                        while (reader.Read())
                        {
                            currPestTreatment = DataMgrTools.BuildMainPestTreatment(reader);
                        }
                        conn.Close();
                    }
                }
                catch (Exception ex)
                {
                    Logger.LogEvent(ex.Message + "-Get(MainPestTreatmentRepository)", System.Diagnostics.EventLogEntryType.Error);
                    conn.Close();
                }
            }
            return currPestTreatment;
        }

        public MainPestTreatmentInfo Add(MainPestTreatmentInfo currPestTreatment)
        {
            try
            {
                using (MySqlConnection conn = new MySqlConnection(mConnStr))
                {
                    using (MySqlCommand cmd = new MySqlCommand())
                    {
                        conn.Open();
                        cmd.Connection = conn;
                        cmd.CommandText = "INSERT INTO main_pests_treatment (maintenance_id, pest_desc, treatment_desc, item_no) " +
                                                               "VALUES (@MaintenanceID, @PestDesc, @TreatmentDesc, @ItemNo)";
                        cmd.Prepare();
                        cmd.Parameters.AddWithValue("@MaintenanceID", currPestTreatment.MaintenanceID);
                        cmd.Parameters.AddWithValue("@PestDesc", currPestTreatment.PestDesc);
                        cmd.Parameters.AddWithValue("@TreatmentDesc", currPestTreatment.TreatmentDesc);
                        cmd.Parameters.AddWithValue("@ItemNo", currPestTreatment.ItemNo);
                        cmd.ExecuteNonQuery();
                        conn.Close();
                    }
                }
            }
            catch (Exception ex)
            {
                Logger.LogEvent(ex.Message + "-Add(MainPestTreatmentRepository)", System.Diagnostics.EventLogEntryType.Error);
            }

            return currPestTreatment;
        }

        public bool Remove(int id)
        {
            bool retVal = false;
            string query = string.Format("DELETE FROM main_pests_treatment WHERE main_pests_treatment_id = {0}", id);

            using (MySqlConnection conn = new MySqlConnection(mConnStr))
            {
                try
                {
                    using (MySqlCommand cmd = new MySqlCommand(query, conn))
                    {
                        conn.Open();
                        if (cmd.ExecuteNonQuery() == 1)
                            retVal = true;
                        else
                            retVal = false;
                        conn.Close();
                    }
                }
                catch (Exception ex)
                {
                    Logger.LogEvent(ex.Message + "-Remove(MainPestTreatmentRepository)", System.Diagnostics.EventLogEntryType.Error);
                }
            }

            return retVal;
        }

        public bool Update(MainPestTreatmentInfo currPestTreatment)
        {
            bool retVal = false;
            try
            {
                using (MySqlConnection conn = new MySqlConnection(mConnStr))
                {
                    using (MySqlCommand cmd = new MySqlCommand())
                    {
                        conn.Open();
                        cmd.Connection = conn;
                        cmd.CommandText = "UPDATE main_pests_treatment SET maintenance_id = @MaintenanceID, pest_desc = @PestDesc, treatment_desc = @TreatmentDesc, item_no = @ItemNo" +
                                                " WHERE main_pest_treatment_id = @MainPestTreatmentID";
                        cmd.Prepare();
                        cmd.Parameters.AddWithValue("@MaintenanceID", currPestTreatment.MaintenanceID);
                        cmd.Parameters.AddWithValue("@PestDesc", currPestTreatment.PestDesc);
                        cmd.Parameters.AddWithValue("@TreatmentDesc", currPestTreatment.TreatmentDesc);
                        cmd.Parameters.AddWithValue("@ItemNo", currPestTreatment.ItemNo);
                        cmd.Parameters.AddWithValue("@MainPestTreatmentID", currPestTreatment.MainPestTreatmentID);

                        if (cmd.ExecuteNonQuery() == 1)
                            retVal = true;
                        else
                            retVal = false;
                        conn.Close();
                    }
                }
            }
            catch (Exception ex)
            {
                Logger.LogEvent(ex.Message + "-Update(MainPestTreatmentRepository)", System.Diagnostics.EventLogEntryType.Error);
            }

            return retVal;
        }

        public MainPestTreatmentInfo GetByPestTreatment(MainPestTreatmentInfo param)
        {
            try
            {
                using (MySqlConnection conn = new MySqlConnection(mConnStr))
                {
                    using (MySqlCommand cmd = new MySqlCommand())
                    {
                        conn.Open();
                        cmd.Connection = conn;
                        cmd.CommandText = "DELETE FROM main_pests_treatment WHERE maintenance_id = @MaintenanceID";
                        cmd.Prepare();
                        cmd.Parameters.AddWithValue("@MaintenanceID", param.MaintenanceID);
                        cmd.ExecuteNonQuery();
                        conn.Close();
                    }
                }
            }
            catch (Exception ex)
            {
                Logger.LogEvent(ex.Message + "-Remove(MainPestTreatmentRepository-GetByPestTretment)", System.Diagnostics.EventLogEntryType.Error);
            }

            return param;
        }
    }
}