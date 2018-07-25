using FMSWebApi.Models;
using MySql.Data.MySqlClient;
using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Web;
using System.IO;
using System.Web.Hosting;
using System.Configuration;
using System.Web.UI.WebControls;
using FMSWebApi.Properties;

namespace FMSWebApi.Repository
{
    public class MaintenanceJobRepository : IMaintenanceJobRepository
    {
        private string mConnStr = "server=localhost;uid=root;pwd=@c3c0M;database=sp;Charset=utf8;max pool size=500;default command timeout=999999;";
        private string mProjName = "SP";
        public IEnumerable<MaintenanceJobInfo> GetAll()
        {
            MySqlConnection conn = new MySqlConnection();
            MySqlCommand cmd = new MySqlCommand();
            List<MaintenanceJobInfo> arrMaintenanceJob = new List<MaintenanceJobInfo>();

            try
            {
                conn.ConnectionString = mConnStr;
                conn.Open();

                cmd.CommandText = "view_maintenance_job";
                cmd.Connection = conn;
                cmd.CommandType = CommandType.TableDirect;
                MySqlDataReader reader = cmd.ExecuteReader();

                if ((reader != null) && (reader.HasRows))
                {
                    while (reader.Read())
                    {
                        MaintenanceJobInfo currMaintenanceJob = DataMgrTools.BuildMaintenanceJob(reader);
                        arrMaintenanceJob.Add(currMaintenanceJob);
                    }
                }
                conn.Close();
            }
            catch (MySqlException ex)
            {
                Logger.LogEvent(mProjName, "GetAll Maintenance Job: " + ex.Message, System.Diagnostics.EventLogEntryType.Error);
            }

            if (conn != null)
                conn.Close();

            return arrMaintenanceJob.ToArray();
        }
        public IEnumerable<MaintenanceJobInfo> GetAllEx()
        {
            MySqlConnection conn = new MySqlConnection();
            MySqlCommand cmd = new MySqlCommand();
            List<MaintenanceJobInfo> arrMaintenanceJob = new List<MaintenanceJobInfo>();

            return arrMaintenanceJob.ToArray();
        }
        public IEnumerable<MaintenanceJobInfo> GetMaintenanceJob(MaintenanceJobInfo param)
        {
            List<MaintenanceJobInfo> arrMaintenanceJob = new List<MaintenanceJobInfo>();
            MaintenanceJobInfo currMaintenanceJob = new MaintenanceJobInfo();

            string query = "SELECT * FROM view_maintenance_job WHERE maintenance_id = @MaintenanceID";
  
            if (!string.IsNullOrEmpty(param.ParamFlag)) query += " and flag IN (" + param.ParamFlag + ")";
            query += " order by timestamp asc";

            using (MySqlConnection conn = new MySqlConnection(mConnStr))
            {
                try
                {
                    using (MySqlCommand cmd = new MySqlCommand(query, conn))
                    {
                        conn.Open();
                        cmd.Prepare();
                        cmd.Parameters.AddWithValue("@MaintenanceID", param.MaintenanceID);

                        using (MySqlDataReader reader = cmd.ExecuteReader())
                        {
                            if ((reader != null) && (reader.HasRows))
                            {
                                while (reader.Read())
                                {
                                    currMaintenanceJob = DataMgrTools.BuildMaintenanceJob(reader);
                                    currMaintenanceJob.Findings = GetFindings(currMaintenanceJob.MaintenanceJobID);
                                    arrMaintenanceJob.Add(currMaintenanceJob);
                                }
                            }
                        }

                        conn.Close();
                    }
                }
                catch (Exception ex)
                {
                    Logger.LogEvent(ex.Message + "-Get(MaintenanceJob)", System.Diagnostics.EventLogEntryType.Error);

                }

            }

            return arrMaintenanceJob;
        }
        private List<FindingsMaintenanceInfo> GetFindings(long maintenanceID)
        {
            List<FindingsMaintenanceInfo> arrFindings = new List<FindingsMaintenanceInfo>();
            FindingsMaintenanceInfo currFindingsMaintenance = new FindingsMaintenanceInfo();
            string query = string.Format("SELECT * FROM findings_maintenance where maintenancejob_id = {0} order by item_no asc", maintenanceID);

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
                            currFindingsMaintenance = DataMgrTools.BuildFindingsMaintenance(reader);
                            arrFindings.Add(currFindingsMaintenance);
                        }
                        conn.Close();
                    }
                }
                catch (Exception ex)
                {
                    Logger.LogEvent(mProjName, "GetFindingsMaintenance: " + ex.Message, System.Diagnostics.EventLogEntryType.Error);
                }
            }

            return arrFindings;
        }
        public MaintenanceJobInfo Get(int maintenanceID)
        {
            MaintenanceJobInfo currMaintenanceJob = new MaintenanceJobInfo();
            string query = string.Format("SELECT * FROM view_maintenance_job WHERE maintenancejob_id = {0}", maintenanceID);

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
                            currMaintenanceJob = DataMgrTools.BuildMaintenanceJob(reader);
                            currMaintenanceJob.Findings = GetFindings(currMaintenanceJob.MaintenanceJobID);
                        }
                        conn.Close();
                    }
                }
                catch (Exception ex)
                {
                    Logger.LogEvent(mProjName, "Get: " + ex.Message, System.Diagnostics.EventLogEntryType.Error);
                    conn.Close();
                }
            }
            return currMaintenanceJob;
        }
        public MaintenanceJobInfo GetMaintenance(int maintenanceID)
        {
            MaintenanceJobInfo currMaintenanceJob = new MaintenanceJobInfo();
            string query = string.Format("SELECT * FROM view_maintenance_job WHERE maintenance_id = {0}", maintenanceID);

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
                            currMaintenanceJob = DataMgrTools.BuildMaintenanceJob(reader);
                            currMaintenanceJob.Findings = GetFindings(currMaintenanceJob.MaintenanceJobID);
                        }
                        conn.Close();
                    }
                }
                catch (Exception ex)
                {
                    Logger.LogEvent(mProjName, "Get Maintenance: " + ex.Message, System.Diagnostics.EventLogEntryType.Error);
                    conn.Close();
                }
            }
            return currMaintenanceJob;
        }
        public MaintenanceJobInfo Add(MaintenanceJobInfo currMaintenanceJob)
        {
            try
            {
                using (MySqlConnection conn = new MySqlConnection(mConnStr))
                {
                    using (MySqlCommand cmd = new MySqlCommand())
                    {
                        conn.Open();
                        cmd.Connection = conn;
                        cmd.CommandText = "INSERT INTO maintenance_job (maintenance_id, alert_date, timestamp, rx_time, flag, job_cancelled, cancel_remarks, isSent, reference_no)" +
                                                              " VALUES (@MaintenanceID, @AlertDate, @Timestamp, @RxTime, @Flag, @JobCancelled, @CancelRemarks, @isSent, @ReferenceNo)";
                        cmd.Prepare();
                        cmd.Parameters.AddWithValue("@MaintenanceID", currMaintenanceJob.MaintenanceID);
                        cmd.Parameters.AddWithValue("@AlertDate", currMaintenanceJob.AlertDate);
                        cmd.Parameters.AddWithValue("@Timestamp", currMaintenanceJob.Timestamp);
                        cmd.Parameters.AddWithValue("@RxTime", currMaintenanceJob.RxTime);
                        cmd.Parameters.AddWithValue("@Flag", currMaintenanceJob.Flag);
                        cmd.Parameters.AddWithValue("@JobCancelled", currMaintenanceJob.JobCancelled);
                        cmd.Parameters.AddWithValue("@CancelRemarks", currMaintenanceJob.CancelRemarks);
                        cmd.Parameters.AddWithValue("@isSent", currMaintenanceJob.isSent);
                        cmd.Parameters.AddWithValue("@ReferenceNo", currMaintenanceJob.ReferenceNo);
                        cmd.ExecuteNonQuery();

                        long id = cmd.LastInsertedId;
                        currMaintenanceJob.MaintenanceJobID = id;

                        conn.Close();
                    }
                }
            }
            catch (Exception ex)
            {
                Logger.LogEvent(ex.Message + "-Add(MaintenanceJobRepository)", System.Diagnostics.EventLogEntryType.Error);
            }

            return currMaintenanceJob;
        }
        public bool Remove(int jobID)
        {
            bool retVal = false;
            string query = string.Format("DELETE FROM maintenance_job WHERE maintenance_id = {0}", jobID);

            try
            {
                using (MySqlConnection conn = new MySqlConnection(mConnStr))
                {
                    using (MySqlCommand cmd = new MySqlCommand(query, conn))
                    {
                        conn.Open();
                        if (cmd.ExecuteNonQuery() == 1)
                            retVal = true;
                        else
                            retVal = false;
                    }
                    conn.Close();
                }
            }
            catch (Exception ex)
            {
                Logger.LogEvent(ex.Message + "-Remove(MaintenanceJobRepository)", System.Diagnostics.EventLogEntryType.Error);
            }

            return retVal;
        }
        public bool Update(MaintenanceJobInfo currMaintenanceJob)
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
                        cmd.CommandText = "UPDATE maintenance_job SET maintenance_id = @MaintenanceID, alert_date = @AlertDate, timestamp = @Timestamp, rx_time = @RxTime, flag = @Flag, job_cancelled = @JobCancelled, cancel_remarks = @CancelRemarks, isSent = @isSent, reference_no = @ReferenceNo " +
                                           "WHERE maintenancejob_id = @MaintenanceJobID";
                        cmd.Prepare();
                        cmd.Parameters.AddWithValue("@MaintenanceID", currMaintenanceJob.MaintenanceID);
                        cmd.Parameters.AddWithValue("@AlertDate", currMaintenanceJob.AlertDate);
                        cmd.Parameters.AddWithValue("@Timestamp", currMaintenanceJob.Timestamp);
                        cmd.Parameters.AddWithValue("@RxTime", currMaintenanceJob.RxTime);
                        cmd.Parameters.AddWithValue("@Flag", currMaintenanceJob.Flag);
                        cmd.Parameters.AddWithValue("@JobCancelled", currMaintenanceJob.JobCancelled);
                        cmd.Parameters.AddWithValue("@CancelRemarks", currMaintenanceJob.CancelRemarks);
                        cmd.Parameters.AddWithValue("@isSent", currMaintenanceJob.isSent);
                        cmd.Parameters.AddWithValue("@ReferenceNo", currMaintenanceJob.ReferenceNo);
                        cmd.Parameters.AddWithValue("@MaintenanceJobID", currMaintenanceJob.MaintenanceJobID);

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
                Logger.LogEvent(ex.Message + "-Update(MaintenanceJobRepository)", System.Diagnostics.EventLogEntryType.Error);
            }

            return retVal;
        }
        public string GetImage(string strName, ref string strFill)
        {
            try
            {
                // loop through image file types
                List<string> arrTypes = new List<string>() { "jpg", "png", "gif", "bmp", "pdf" };
                foreach (string strType in arrTypes)
                {
                    string strTemp = String.Format("{0}.{1}", strName, strType);
                    string strFile = String.Format("{0}images\\{1}", HostingEnvironment.ApplicationPhysicalPath, strTemp);
                    //string strUri = String.Format("{0}/images/{1}", ConfigurationManager.AppSettings["ServerPath"], strTemp);
                    string strUri = String.Format("{0}/images/{1}", "http://117.120.7.120/spwebapi", strTemp);

                    // check file path
                    if (File.Exists(strFile))
                    {
                        // return image path
                        strFill = "Uniform";
                        return String.Format("{0}?dt={1:ddMMMyyHHmmss}", strUri, File.GetLastWriteTimeUtc(strFile));
                    }
                }
            }
            catch (Exception ex)
            {
                // log error
                Logger.LogEvent("Get Image: ", ex.Message, System.Diagnostics.EventLogEntryType.Error);
            }

            // image file not found
            strFill = "None";
            return strName;
        }

    }
}