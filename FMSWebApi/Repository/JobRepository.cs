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
    public class JobRepository : IJobRepository
    {
        private string mConnStr = "server=localhost;uid=root;pwd=@c3c0M;database=sp;Charset=utf8;max pool size=500;";
        private string mProjName = "SP";
        public IEnumerable<JobInfo> GetAll()
        {
            MySqlConnection conn = new MySqlConnection();
            MySqlCommand cmd = new MySqlCommand();
            List<JobInfo> arrJobs = new List<JobInfo>();

            return arrJobs.ToArray();
        }

        public IEnumerable<JobInfo> ClearJobs()
        {
            MySqlConnection conn = new MySqlConnection();
            MySqlCommand cmd = new MySqlCommand();
            List<JobInfo> arrJobs = new List<JobInfo>();

            return arrJobs.ToArray();
        }

        public IEnumerable<JobInfo> GetByCompany(JobInfo param)
        {
            List<JobInfo> arrJobs = new List<JobInfo>();
            JobInfo currJob = new JobInfo();
            object objTemp = new object();

            string query = "SELECT * FROM view_jobs WHERE (timestamp between @StartTS and @EndTS)";

            if (param.AssetResellerID > 0) query += " and reseller_id = @AssetResellerID";
            if (param.AssetCompanyID > 0) query += " and company_id = @AssetCompanyID";
            if (param.Flag > 0) query += " and flag >= @Flag";
            if (!string.IsNullOrEmpty(param.FlagValue)) query += " and flag_value = @FlagValue";
            if (param.UserID > 0) query += " and user_id = @UserID";
            if (param.DriverID > 0) query += " and driver_id = @DriverID";
            if (param.AssetID > 0) query += " and asset_id = @AssetID";
            else if (!string.IsNullOrEmpty(param.Asset)) query += " and asset_id = (SELECT asset_id FROM view_assets WHERE name = @AssetName)";

            query += " order by timestamp desc";

            using (MySqlConnection conn = new MySqlConnection(mConnStr))
            {
                try
                {
                    using (MySqlCommand cmd = new MySqlCommand(query, conn))
                    {
                        conn.Open();
                        cmd.Prepare();
                        cmd.Parameters.AddWithValue("@StartTS", param.Timestamp);
                        cmd.Parameters.AddWithValue("@EndTS", param.RxTime);
                        cmd.Parameters.AddWithValue("@AssetResellerID", param.AssetResellerID);
                        cmd.Parameters.AddWithValue("@AssetCompanyID", param.AssetCompanyID);
                        cmd.Parameters.AddWithValue("@FlagValue", param.FlagValue);
                        cmd.Parameters.AddWithValue("@UserID", param.UserID);
                        cmd.Parameters.AddWithValue("@DriverID", param.DriverID);
                        cmd.Parameters.AddWithValue("@Flag", param.Flag);
                        if (param.AssetID > 0) cmd.Parameters.AddWithValue("@AssetID", param.AssetID);
                        else if (!string.IsNullOrEmpty(param.Asset)) cmd.Parameters.AddWithValue("@AssetName", param.Asset);

                        using (MySqlDataReader reader = cmd.ExecuteReader())
                        {
                            if ((reader != null) && (reader.HasRows))
                            {
                                while (reader.Read())
                                {
                                    currJob = DataMgrTools.BuildJob(reader);
                                    string strFill = "";
                                    currJob.Image = GetImage(String.Format("signatures/{0}", currJob.JobID), ref strFill);
                                    currJob.ImageFill = strFill;
                                    currJob.Pest = GetPestTreatment(currJob.JobID);
                                    currJob.Findings = GetFindings(currJob.JobID);
                                    arrJobs.Add(currJob);
                                }
                            }
                        }

                        conn.Close();
                    }
                }
                catch (Exception ex)
                {
                    Logger.LogEvent(ex.Message + "-Get(ByCompany)", System.Diagnostics.EventLogEntryType.Error);

                }

                if (param.AssetCompanyID == 1 || param.AssetCompanyID == 3)
                {
                    try
                    {
                        using (MySqlCommand cmd = new MySqlCommand(query, conn))
                        {
                            conn.Open();
                            for (int i = 0; i < arrJobs.Count; i++)
                            {
                                query = string.Format("SELECT * FROM area_covered where postal_sector = {0}", arrJobs[i].Postal.ToString().Substring(0, 2));
                                cmd.CommandText = query;
                                cmd.Connection = conn;
                                cmd.CommandType = CommandType.Text;
                                using (MySqlDataReader reader = cmd.ExecuteReader())
                                {
                                    AcInfo currAc = new AcInfo();
                                    if ((reader != null) && (reader.HasRows))
                                    {
                                        while (reader.Read())
                                        {
                                            currAc = DataMgrTools.BuildAc(reader);
                                            arrJobs[i].AcInfo = currAc;
                                        }
                                    }
                                    else
                                    {
                                        arrJobs[i].AcInfo = currAc;
                                    }

                                }
                            }
                            conn.Close();
                        }
                    }
                    catch (Exception ex)
                    {
                        Logger.LogEvent("GetByCompany-areacovered: " + ex.Message, System.Diagnostics.EventLogEntryType.Error);
                    }
                }

                if (param.AssetCompanyID == 2)
                {
                    try
                    {
                        using (MySqlCommand cmd = new MySqlCommand(query, conn))
                        {
                            conn.Open();
                            for (int i = 0; i < arrJobs.Count; i++)
                            {
                                query = string.Format("SELECT * FROM area_covered_my WHERE {0} BETWEEN lower_bound AND upper_bound order by lower_bound desc LIMIT 1", arrJobs[i].Postal);
                                cmd.CommandText = query;
                                cmd.Connection = conn;
                                cmd.CommandType = CommandType.Text;
                                using (MySqlDataReader reader = cmd.ExecuteReader())
                                {
                                    AcMyInfo currAcMy = new AcMyInfo();
                                    if ((reader != null) && (reader.HasRows))
                                    {
                                        while (reader.Read())
                                        {
                                            currAcMy = DataMgrTools.BuildAcMy(reader);
                                            arrJobs[i].AcMyInfo = currAcMy;
                                        }
                                    }
                                    else
                                    {
                                        arrJobs[i].AcMyInfo = currAcMy;
                                    }

                                }
                            }
                            conn.Close();
                        }
                    }
                    catch (Exception ex)
                    {
                        Logger.LogEvent("GetByCompany-areacovered: " + ex.Message, System.Diagnostics.EventLogEntryType.Error);
                    }
                }

            }

            return arrJobs;
        }
        private List<PestTreatmentInfo> GetPestTreatment(long jobID)
        {
            List<PestTreatmentInfo> arrPestTreatment = new List<PestTreatmentInfo>();
            PestTreatmentInfo currPestTreatment = new PestTreatmentInfo();
            string query = string.Format("SELECT * FROM pests_treatment where job_id = {0} order by item_no asc", jobID);

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
                            currPestTreatment = DataMgrTools.BuildPestTreatment(reader);
                            arrPestTreatment.Add(currPestTreatment);
                        }
                        conn.Close();
                    }
                }
                catch (Exception ex)
                {
                    Logger.LogEvent(mProjName, "GetPestTreatment: " + ex.Message, System.Diagnostics.EventLogEntryType.Error);
                }
            }

            return arrPestTreatment;
        }
        private List<FindingsInfo> GetFindings(long jobID)
        {
            List<FindingsInfo> arrFindings = new List<FindingsInfo>();
            FindingsInfo currPestTreatment = new FindingsInfo();
            string query = string.Format("SELECT * FROM findings where job_id = {0} order by item_no asc", jobID);

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
                            currPestTreatment = DataMgrTools.BuildFindings(reader);
                            arrFindings.Add(currPestTreatment);
                        }
                        conn.Close();
                    }
                }
                catch (Exception ex)
                {
                    Logger.LogEvent(mProjName, "GetFindings: " + ex.Message, System.Diagnostics.EventLogEntryType.Error);
                }
            }

            return arrFindings;
        }
        public IEnumerable<JobInfo> GetByCompanyEx(JobInfo param)
        {
            List<JobInfo> arrJobs = new List<JobInfo>();
            JobInfo currJob = new JobInfo();
            object objTemp = new object();

            string query = "SELECT * FROM view_jobs WHERE timestamp >= CURDATE()";

            if (param.AssetResellerID > 0) query += " and reseller_id = @AssetResellerID";
            if (param.AssetCompanyID > 0) query += " and company_id = @AssetCompanyID";
            if (param.Flag > 0) query += " and flag >= @Flag";
            if (param.AssetID > 0) query += " and asset_id = @AssetID";
            else if (!string.IsNullOrEmpty(param.Asset)) query += " and asset_id = (SELECT asset_id FROM view_assets WHERE name = @AssetName)";

            using (MySqlConnection conn = new MySqlConnection(mConnStr))
            {
                try
                {
                    using (MySqlCommand cmd = new MySqlCommand(query, conn))
                    {
                        conn.Open();
                        cmd.Prepare();
                        cmd.Parameters.AddWithValue("@AssetResellerID", param.AssetResellerID);
                        cmd.Parameters.AddWithValue("@AssetCompanyID", param.AssetCompanyID);
                        cmd.Parameters.AddWithValue("@Flag", param.Flag);
                        if (param.AssetID > 0) cmd.Parameters.AddWithValue("@AssetID", param.AssetID);
                        else if (!string.IsNullOrEmpty(param.Asset)) cmd.Parameters.AddWithValue("@AssetName", param.Asset);

                        using (MySqlDataReader reader = cmd.ExecuteReader())
                        {
                            if ((reader != null) && (reader.HasRows))
                            {
                                while (reader.Read())
                                {
                                    currJob = DataMgrTools.BuildJob(reader);
                                    string strFill = "";
                                    currJob.Image = GetImage(String.Format("signatures/{0}", currJob.JobID), ref strFill);
                                    currJob.ImageFill = strFill;
                                    arrJobs.Add(currJob);
                                }
                            }
                        }

                        conn.Close();
                    }
                }
                catch (Exception ex)
                {
                    Logger.LogEvent(ex.Message + "-Get(ByCompany)", System.Diagnostics.EventLogEntryType.Error);

                }

                try
                {
                    using (MySqlCommand cmd = new MySqlCommand(query, conn))
                    {
                        conn.Open();
                        for (int i = 0; i < arrJobs.Count; i++)
                        {
                            query = string.Format("SELECT * FROM view_drivers where asset_id = {0}", arrJobs[i].AssetID);

                            cmd.CommandText = query;
                            cmd.Connection = conn;
                            cmd.CommandType = CommandType.Text;
                            using (MySqlDataReader reader = cmd.ExecuteReader())
                            {
                                DriverInfo currDriver = new DriverInfo();
                                if ((reader != null) && (reader.HasRows))
                                {
                                    while (reader.Read())
                                    {
                                        currDriver = DataMgrTools.BuildDriver(reader);
                                        string strFill = "";
                                        currDriver.Image = GetImage(String.Format("drivers/{0}", currDriver.DriverID), ref strFill);
                                        currDriver.ImageFill = strFill;
                                        arrJobs[i].DriverInfo = currDriver;
                                    }
                                }
                                else
                                {
                                    arrJobs[i].DriverInfo = currDriver;

                                }

                            }
                        }
                        conn.Close();
                    }
                }
                catch (Exception ex)
                {
                    Logger.LogEvent("GetByCompany-view_driver: " + ex.Message, System.Diagnostics.EventLogEntryType.Error);
                }
            }

            return arrJobs;
        }

        public IEnumerable<JobInfo> GetJobList(JobInfo param)
        {
            List<JobInfo> arrJobs = new List<JobInfo>();
            JobInfo currJob = new JobInfo();
            object objTemp = new object();

            string query = "SELECT * FROM view_jobs WHERE (timestamp between @StartTS and @EndTS)";

            if (param.AssetResellerID > 0) query += " and reseller_id = @AssetResellerID";
            if (param.AssetCompanyID > 0) query += " and company_id = @AssetCompanyID";

            if (param.DriverID > 0) query += " and driver_id = @DriverID";

            using (MySqlConnection conn = new MySqlConnection(mConnStr))
            {
                try
                {
                    using (MySqlCommand cmd = new MySqlCommand(query, conn))
                    {
                        conn.Open();
                        cmd.Prepare();
                        cmd.Parameters.AddWithValue("@StartTS", param.Timestamp);
                        cmd.Parameters.AddWithValue("@EndTS", param.RxTime);
                        cmd.Parameters.AddWithValue("@AssetResellerID", param.AssetResellerID);
                        cmd.Parameters.AddWithValue("@AssetCompanyID", param.AssetCompanyID);
                        cmd.Parameters.AddWithValue("@DriverID", param.DriverID);

                        using (MySqlDataReader reader = cmd.ExecuteReader())
                        {
                            if ((reader != null) && (reader.HasRows))
                            {
                                while (reader.Read())
                                {
                                    currJob = DataMgrTools.BuildJob(reader);
                                    string strFill = "";
                                    currJob.Image = GetImage(String.Format("signatures/{0}", currJob.JobID), ref strFill);
                                    currJob.ImageFill = strFill;
                                    arrJobs.Add(currJob);
                                }
                            }
                        }

                        conn.Close();
                    }
                }
                catch (Exception ex)
                {
                    Logger.LogEvent(ex.Message + "-Get(ByCompany)", System.Diagnostics.EventLogEntryType.Error);

                }

                try
                {
                    using (MySqlCommand cmd = new MySqlCommand(query, conn))
                    {
                        conn.Open();
                        for (int i = 0; i < arrJobs.Count; i++)
                        {
                            query = string.Format("SELECT * FROM view_drivers where asset_id = {0}", arrJobs[i].AssetID);

                            cmd.CommandText = query;
                            cmd.Connection = conn;
                            cmd.CommandType = CommandType.Text;
                            using (MySqlDataReader reader = cmd.ExecuteReader())
                            {
                                DriverInfo currDriver = new DriverInfo();
                                if ((reader != null) && (reader.HasRows))
                                {
                                    while (reader.Read())
                                    {
                                        currDriver = DataMgrTools.BuildDriver(reader);
                                        string strFill = "";
                                        currDriver.Image = GetImage(String.Format("drivers/{0}", currDriver.DriverID), ref strFill);
                                        currDriver.ImageFill = strFill;
                                        arrJobs[i].DriverInfo = currDriver;
                                    }
                                }
                                else
                                {
                                    arrJobs[i].DriverInfo = currDriver;

                                }

                            }
                        }
                        conn.Close();
                    }
                }
                catch (Exception ex)
                {
                    Logger.LogEvent("GetByCompany-view_driver: " + ex.Message, System.Diagnostics.EventLogEntryType.Error);
                }
            }

            return arrJobs;
        }

        public IEnumerable<JobInfo> GetIncompleteJobs(JobInfo param)
        {
            List<JobInfo> arrJobs = new List<JobInfo>();
            JobInfo currJob = new JobInfo();
            object objTemp = new object();

            string query = "SELECT * FROM view_jobs WHERE timestamp <= @StartTS";

            if (param.AssetResellerID > 0) query += " and reseller_id = @AssetResellerID";
            if (param.AssetCompanyID > 0) query += " and company_id = @AssetCompanyID";
            if (param.Flag > 0) query += " and flag >= @Flag";
            if (param.AssetID > 0) query += " and asset_id = @AssetID";

            using (MySqlConnection conn = new MySqlConnection(mConnStr))
            {
                try
                {
                    using (MySqlCommand cmd = new MySqlCommand(query, conn))
                    {
                        conn.Open();
                        cmd.Prepare();
                        cmd.Parameters.AddWithValue("@StartTS", param.Timestamp);
                        cmd.Parameters.AddWithValue("@AssetResellerID", param.AssetResellerID);
                        cmd.Parameters.AddWithValue("@AssetCompanyID", param.AssetCompanyID);
                        cmd.Parameters.AddWithValue("@Flag", param.Flag);
                        if (param.AssetID > 0) cmd.Parameters.AddWithValue("@AssetID", param.AssetID);

                        using (MySqlDataReader reader = cmd.ExecuteReader())
                        {
                            if ((reader != null) && (reader.HasRows))
                            {
                                while (reader.Read())
                                {
                                    currJob = DataMgrTools.BuildJob(reader);
                                    string strFill = "";
                                    currJob.Image = GetImage(String.Format("signatures/{0}", currJob.JobID), ref strFill);
                                    currJob.ImageFill = strFill;
                                    arrJobs.Add(currJob);
                                }
                            }
                        }

                        conn.Close();
                    }
                }
                catch (Exception ex)
                {
                    Logger.LogEvent(ex.Message + "-Get(ByCompany)", System.Diagnostics.EventLogEntryType.Error);

                }

                try
                {
                    using (MySqlCommand cmd = new MySqlCommand(query, conn))
                    {
                        conn.Open();
                        for (int i = 0; i < arrJobs.Count; i++)
                        {
                            query = string.Format("SELECT * FROM view_drivers where asset_id = {0}", arrJobs[i].AssetID);

                            cmd.CommandText = query;
                            cmd.Connection = conn;
                            cmd.CommandType = CommandType.Text;
                            using (MySqlDataReader reader = cmd.ExecuteReader())
                            {
                                DriverInfo currDriver = new DriverInfo();
                                if ((reader != null) && (reader.HasRows))
                                {
                                    while (reader.Read())
                                    {
                                        currDriver = DataMgrTools.BuildDriver(reader);
                                        string strFill = "";
                                        currDriver.Image = GetImage(String.Format("drivers/{0}", currDriver.DriverID), ref strFill);
                                        currDriver.ImageFill = strFill;
                                        arrJobs[i].DriverInfo = currDriver;
                                    }
                                }
                                else
                                {
                                    arrJobs[i].DriverInfo = currDriver;

                                }

                            }
                        }
                        conn.Close();
                    }
                }
                catch (Exception ex)
                {
                    Logger.LogEvent("SearchIncompleteJobs: " + ex.Message, System.Diagnostics.EventLogEntryType.Error);
                }
            }

            return arrJobs;
        }

        public JobInfo GetIncompleteJobsEx(JobInfo item)
        {
            try
            {
                using (MySqlConnection conn = new MySqlConnection(mConnStr))
                {
                    using (MySqlCommand cmd = new MySqlCommand())
                    {
                        conn.Open();
                        cmd.Connection = conn;
                        cmd.CommandText = "UPDATE jobs SET flag = @Flag WHERE timestamp < @Timestamp and flag > 0 and asset_id = @AssetID";
                        cmd.Prepare();
                        cmd.Parameters.AddWithValue("@Timestamp", item.Timestamp);
                        cmd.Parameters.AddWithValue("@Flag", 0);
                        cmd.Parameters.AddWithValue("@AssetID", item.AssetID);
                        cmd.ExecuteNonQuery();
                        conn.Close();
                    }
                }
            }
            catch (Exception ex)
            {
                Logger.LogEvent(ex.Message + "-GetIncompleteJobsEx", System.Diagnostics.EventLogEntryType.Error);
            }

            return item;
        }

        public JobInfo Get(int jobID)
        {
            List<JobInfo> arrJobs = new List<JobInfo>();
            JobInfo currJob = new JobInfo();
            string query = string.Format("SELECT * FROM view_jobs WHERE job_id = {0}", jobID);

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
                            currJob = DataMgrTools.BuildJob(reader);
                            string strFill = "";
                            currJob.Image = GetImage(String.Format("signatures/{0}", currJob.JobID), ref strFill);
                            currJob.ImageFill = strFill;
                            currJob.Pest = GetPestTreatment(currJob.JobID);
                            currJob.Findings = GetFindings(currJob.JobID);
                            arrJobs.Add(currJob);
                        }
                        conn.Close();
                    }
                }
                catch (Exception ex)
                {
                    Logger.LogEvent(mProjName, "Get: " + ex.Message, System.Diagnostics.EventLogEntryType.Error);
                    conn.Close();
                }

                try
                {
                    using (MySqlCommand cmd = new MySqlCommand(query, conn))
                    {
                        conn.Open();
                        for (int i = 0; i < arrJobs.Count; i++)
                        {
                            query = string.Format("SELECT * FROM area_covered where postal_sector = {0}", arrJobs[i].Postal.ToString().Substring(0, 2));
                            cmd.CommandText = query;
                            cmd.Connection = conn;
                            cmd.CommandType = CommandType.Text;
                            using (MySqlDataReader reader = cmd.ExecuteReader())
                            {
                                AcInfo currAc = new AcInfo();
                                if ((reader != null) && (reader.HasRows))
                                {
                                    while (reader.Read())
                                    {
                                        currAc = DataMgrTools.BuildAc(reader);
                                        arrJobs[i].AcInfo = currAc;
                                    }
                                }
                                else
                                {
                                    arrJobs[i].AcInfo = currAc;
                                }

                            }
                        }
                        conn.Close();
                    }
                }
                catch (Exception ex)
                {
                    Logger.LogEvent("Get-areacovered: " + ex.Message, System.Diagnostics.EventLogEntryType.Error);
                }
            }
            return currJob;
        }

        public JobInfo Add(JobInfo currJob)
        {
            try
            {
                using (MySqlConnection conn = new MySqlConnection(mConnStr))
                {
                    using (MySqlCommand cmd = new MySqlCommand())
                    {
                        conn.Open();
                        cmd.Connection = conn;
                        cmd.CommandText = "INSERT INTO jobs (job_number, company, timestamp, rx_time, company_id, reseller_id, asset_id, amount, pic, destination, " + 
                                                            "phone, unit, remarks, flag, receipt_number, user_id, driver_id, postal, job_accepted, job_completed, " +
                                                            "cus_email, site, received_amount, payment_type, recommendations, reference_no) " +
                                                    "VALUES (@JobNumber, @Company, @Timestamp, @RxTime, @AssetCompanyID, @AssetResellerID, @AssetID, @Amount, @PIC, @Destination, " + 
                                                            "@Phone, @Unit, @Remarks, @Flag, @Receipt, @UserID, @DriverID, @Postal, @JobAccepted, @JobCompleted, " +
                                                            "@CusEmail, @Site, @ReceivedAmount, @PaymentType, @Recommendations, @ReferenceNo)";
                        cmd.Prepare();
                        cmd.Parameters.AddWithValue("@JobNumber", currJob.JobNumber);
                        cmd.Parameters.AddWithValue("@Company", currJob.Company);
                        cmd.Parameters.AddWithValue("@Timestamp", currJob.Timestamp);
                        cmd.Parameters.AddWithValue("@RxTime", currJob.RxTime);
                        cmd.Parameters.AddWithValue("@AssetCompanyID", currJob.AssetCompanyID);
                        cmd.Parameters.AddWithValue("@AssetResellerID", currJob.AssetResellerID);
                        cmd.Parameters.AddWithValue("@AssetID", currJob.AssetID);
                        cmd.Parameters.AddWithValue("@Amount", currJob.Amount);
                        cmd.Parameters.AddWithValue("@PIC", currJob.PIC);
                        cmd.Parameters.AddWithValue("@Destination", currJob.Destination);
                        cmd.Parameters.AddWithValue("@Phone", currJob.Phone);
                        cmd.Parameters.AddWithValue("@Unit", currJob.Unit);
                        cmd.Parameters.AddWithValue("@Remarks", currJob.Remarks);
                        cmd.Parameters.AddWithValue("@Flag", currJob.Flag);
                        cmd.Parameters.AddWithValue("@Receipt", "");
                        cmd.Parameters.AddWithValue("@UserID", currJob.UserID);
                        cmd.Parameters.AddWithValue("@DriverID", currJob.DriverID);
                        cmd.Parameters.AddWithValue("@Postal", currJob.Postal);
                        cmd.Parameters.AddWithValue("@JobAccepted", null);
                        cmd.Parameters.AddWithValue("@JobCompleted", null);
                        cmd.Parameters.AddWithValue("@CusEmail", currJob.CusEmail);
                        cmd.Parameters.AddWithValue("@Site", currJob.Site);
                        cmd.Parameters.AddWithValue("@ReceivedAmount", currJob.ReceivedAmount);
                        cmd.Parameters.AddWithValue("@PaymentType", currJob.PaymentType);
                        cmd.Parameters.AddWithValue("@Recommendations", currJob.Recommendations);
                        cmd.Parameters.AddWithValue("@ReferenceNo", currJob.ReferenceNo);
                        cmd.ExecuteNonQuery();

                        long id = cmd.LastInsertedId;
                        currJob.JobID = id;

                        conn.Close();
                    }
                }
            }
            catch (Exception ex)
            {
                Logger.LogEvent(ex.Message + "-Add(JobRepository)", System.Diagnostics.EventLogEntryType.Error);
            }

            return currJob;
        }

        public bool Remove(int jobID)
        {
            bool retVal = false;
            string query = "";

            try
            {
                query = string.Format("DELETE FROM jobs WHERE job_id = {0}", jobID);

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
                Logger.LogEvent(ex.Message + "-Remove(JobRepository)", System.Diagnostics.EventLogEntryType.Error);
            }

            try
            {
                query = string.Format("DELETE FROM pests_treatment WHERE job_id = {0}", jobID);

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
                Logger.LogEvent(ex.Message + "-Remove(PestTreatmentRepository)", System.Diagnostics.EventLogEntryType.Error);
            }

            return retVal;
        }

        public bool Update(JobInfo currJob)
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
                        cmd.CommandText = "UPDATE jobs SET job_number = @JobNumber, company = @Company, timestamp = @Timestamp, rx_time = @RxTime, company_id = @AssetCompanyID, reseller_id = @AssetResellerID, asset_id = @AssetID, amount = @Amount, pic = @PIC, destination = @Destination, " +
                                                            "phone = @Phone, unit = @Unit, remarks = @Remarks, flag = @Flag, receipt_number = @Receipt, user_id = @UserID, driver_id = @DriverID, postal = @Postal, job_accepted = @JobAccepted, job_completed = @JobCompleted, " +
                                                            "cus_email = @CusEmail, site = @Site, received_amount = @ReceivedAmount, payment_type = @PaymentType, recommendations = @Recommendations, reference_no = @ReferenceNo WHERE job_id = @JobID";
                        cmd.Prepare();
                        cmd.Parameters.AddWithValue("@JobNumber", currJob.JobNumber);
                        cmd.Parameters.AddWithValue("@Company", currJob.Company);
                        cmd.Parameters.AddWithValue("@Timestamp", currJob.Timestamp);
                        cmd.Parameters.AddWithValue("@RxTime", currJob.RxTime);
                        cmd.Parameters.AddWithValue("@AssetCompanyID", currJob.AssetCompanyID);
                        cmd.Parameters.AddWithValue("@AssetResellerID", currJob.AssetResellerID);
                        cmd.Parameters.AddWithValue("@AssetID", currJob.AssetID);
                        cmd.Parameters.AddWithValue("@Amount", currJob.Amount);
                        cmd.Parameters.AddWithValue("@PIC", currJob.PIC);
                        cmd.Parameters.AddWithValue("@Destination", currJob.Destination);
                        cmd.Parameters.AddWithValue("@Phone", currJob.Phone);
                        cmd.Parameters.AddWithValue("@Unit", currJob.Unit);
                        cmd.Parameters.AddWithValue("@Remarks", currJob.Remarks);
                        cmd.Parameters.AddWithValue("@Flag", currJob.Flag);
                        cmd.Parameters.AddWithValue("@Receipt", currJob.Receipt);
                        cmd.Parameters.AddWithValue("@UserID", currJob.UserID);
                        cmd.Parameters.AddWithValue("@DriverID", currJob.DriverID);
                        cmd.Parameters.AddWithValue("@Postal", currJob.Postal);
                        cmd.Parameters.AddWithValue("@JobAccepted", currJob.JobAccepted);
                        cmd.Parameters.AddWithValue("@JobCompleted", currJob.JobCompleted);
                        cmd.Parameters.AddWithValue("@CusEmail", currJob.CusEmail);
                        cmd.Parameters.AddWithValue("@Site", currJob.Site);
                        cmd.Parameters.AddWithValue("@ReceivedAmount", currJob.ReceivedAmount);
                        cmd.Parameters.AddWithValue("@PaymentType", currJob.PaymentType);
                        cmd.Parameters.AddWithValue("@Recommendations", currJob.Recommendations);
                        cmd.Parameters.AddWithValue("@ReferenceNo", currJob.ReferenceNo);
                        cmd.Parameters.AddWithValue("@JobID", currJob.JobID);

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
                Logger.LogEvent(ex.Message + "-Update(JobRepository)", System.Diagnostics.EventLogEntryType.Error);
            }

            return retVal;
        }

        /// <summary>
        /// gets image file
        /// </summary>
        /// <param name="strName"></param>
        /// <param name="strDefault"></param>
        /// <param name="strFill"></param>
        /// <returns></returns>
        public string GetImage(string strName, ref string strFill)
        {
            try
            {
                // loop through image file types
                List<string> arrTypes = new List<string>() { "jpg", "png", "gif", "bmp" };
                foreach (string strType in arrTypes)
                {
                    string strTemp = String.Format("{0}.{1}", strName, strType);
                    string strFile = String.Format("{0}images\\{1}", HostingEnvironment.ApplicationPhysicalPath, strTemp);
                    //string strUri = String.Format("{0}/images/{1}", ConfigurationManager.AppSettings["ServerPath"], strTemp);
                    string strUri = String.Format("{0}/images/{1}", "http://117.120.7.119/spwebapi", strTemp);

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


        public JobInfo GetByAssetID(int assetID)
        {

            JobInfo currJob = new JobInfo();
            string query = string.Format("SELECT * FROM view_jobs WHERE asset_id = @AssetID and flag >= 1 order by timestamp desc");


            using (MySqlConnection conn = new MySqlConnection(mConnStr))
            {
                try
                {
                    using (MySqlCommand cmd = new MySqlCommand())
                    {
                        conn.Open();
                        cmd.Connection = conn;
                        cmd.CommandText = query;
                        cmd.Prepare();
                        cmd.Parameters.AddWithValue("@AssetID", assetID);
                        MySqlDataReader reader = cmd.ExecuteReader();

                        while (reader.Read())
                        {
                            currJob = DataMgrTools.BuildJob(reader);
                        }
                        conn.Close();
                    }
                }
                catch (Exception ex)
                {
                    Logger.LogEvent(ex.Message + "-GetByAssetID", System.Diagnostics.EventLogEntryType.Error);
                    conn.Close();
                }
            }
            return currJob;
        }

        public JobInfo UpdateJobByZone(JobInfo currJob)
        {
            try
            {
                using (MySqlConnection conn = new MySqlConnection(mConnStr))
                {
                    using (MySqlCommand cmd = new MySqlCommand())
                    {
                        conn.Open();
                        cmd.Connection = conn;
                        cmd.CommandText = "UPDATE jobs SET job_number = @JobNumber, company = @Company, timestamp = @Timestamp, rx_time = @RxTime, company_id = @AssetCompanyID, reseller_id = @AssetResellerID, asset_id = @AssetID, " +
                           "amount = @Amount, pic = @PIC, destination = @Destination, phone = @Phone, unit = @Unit, remarks = @Remarks, flag = @Flag, " +
                           "receipt_number = @Receipt, user_id = @UserID, driver_id = @DriverID WHERE job_id = @JobID";
                        cmd.Prepare();
                        cmd.Parameters.AddWithValue("@JobNumber", currJob.JobNumber);
                        cmd.Parameters.AddWithValue("@Company", currJob.Company);
                        cmd.Parameters.AddWithValue("@Timestamp", currJob.Timestamp);
                        cmd.Parameters.AddWithValue("@RxTime", currJob.RxTime);
                        cmd.Parameters.AddWithValue("@AssetCompanyID", currJob.AssetCompanyID);
                        cmd.Parameters.AddWithValue("@AssetResellerID", currJob.AssetResellerID);
                        cmd.Parameters.AddWithValue("@AssetID", currJob.AssetID);
                        cmd.Parameters.AddWithValue("@Amount", currJob.Amount);
                        cmd.Parameters.AddWithValue("@PIC", currJob.PIC);
                        cmd.Parameters.AddWithValue("@Destination", currJob.Destination);
                        cmd.Parameters.AddWithValue("@Phone", currJob.Phone);
                        cmd.Parameters.AddWithValue("@Unit", currJob.Unit);
                        cmd.Parameters.AddWithValue("@Remarks", currJob.Remarks);
                        cmd.Parameters.AddWithValue("@Flag", currJob.Flag);
                        cmd.Parameters.AddWithValue("@Receipt", "");
                        cmd.Parameters.AddWithValue("@UserID", currJob.UserID);
                        cmd.Parameters.AddWithValue("@DriverID", currJob.DriverID);
                        cmd.Parameters.AddWithValue("@Postal", currJob.Postal);
                        cmd.Parameters.AddWithValue("@JobID", currJob.JobID);
                        cmd.ExecuteNonQuery();
                        conn.Close();
                    }
                }
            }
            catch (Exception ex)
            {
                Logger.LogEvent(ex.Message + "-UpdateJobByZone(JobRepository)", System.Diagnostics.EventLogEntryType.Error);
            }

            return currJob;
        }

    }
}