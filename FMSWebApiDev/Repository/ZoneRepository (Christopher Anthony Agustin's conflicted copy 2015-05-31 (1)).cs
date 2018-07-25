﻿using FMSWebApi.Models;
using MySql.Data.MySqlClient;
using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Web;

namespace FMSWebApi.Repository
{
    public class ZoneRepository : IZoneRepository
    {
        //private string mConnStr = "server=127.0.0.1;uid=root;pwd=12345;database=test;";
        private string mConnStr = "server=103.237.168.119;uid=root;pwd=@c3c0M;database=fms;";
        private string mProjName = "FMS";
        public IEnumerable<ZoneInfo> GetAll()
        {
            MySqlConnection conn = new MySqlConnection();
            MySqlCommand cmd = new MySqlCommand();
            List<ZoneInfo> arrAssets = new List<ZoneInfo>();

            try
            {
                conn.ConnectionString = mConnStr;
                conn.Open();

                cmd.CommandText = "view_zones";
                cmd.Connection = conn;
                cmd.CommandType = CommandType.TableDirect;
                MySqlDataReader reader = cmd.ExecuteReader();

                //Console.WriteLine(String.Format("Total Data: {0}", dbRdr.FieldCount));

                if ((reader != null) && (reader.HasRows))
                {
                    while (reader.Read())
                    {
                        ZoneInfo currAsset = DataMgrTools.BuildZone(reader);
                        arrAssets.Add(currAsset);
                    }
                }
                conn.Close();
            }
            catch (MySqlException ex)
            {
                Logger.LogEvent(ex.Message + "-GetAll(ZoneRepository)", System.Diagnostics.EventLogEntryType.Error);
            }

            if (conn != null)
                conn.Close();

            return arrAssets.ToArray();
        }

        public ZoneInfo Get(int id)
        {
            ZoneInfo currAsset = new ZoneInfo();
            string query = string.Format("SELECT * FROM view_zones WHERE zone_id = {0}", id);

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
                            currAsset = DataMgrTools.BuildZone(reader);
                        }
                        conn.Close();
                    }
                }
                catch (Exception ex)
                {
                    Logger.LogEvent(ex.Message + "-Get(ZoneRepository)", System.Diagnostics.EventLogEntryType.Error);
                    conn.Close();
                }
            }
            return currAsset;
        }

        public ZoneInfo Add(ZoneInfo currZone)
        {
            try
            {
                using (MySqlConnection conn = new MySqlConnection(mConnStr))
                {
                    using (MySqlCommand cmd = new MySqlCommand())
                    {
                        conn.Open();
                        cmd.Connection = conn;

                        //get company id
                        int coyId = RepoHelpers.GetCompanyId(currZone.Company);
                        if (coyId < 0)
                        {
                            currZone.ErrorMessage = Consts.ERR_COMPANYERROR;
                            return currZone;
                        }

                        cmd.CommandText = "INSERT INTO zones (name, type_id, perimeter, cell_ids, loc, company_id, " +
                            "created_date, modified_date, created_by, modified_by, created_byName, modified_byName) " +
                            "VALUES (@Name, @TypeID, @Perimeter, @CellIds, @Location, @CompanyID, @CreatedDate, @LastModified, " +
                            "@CreatedID, @ModifiedID, @CreatedName, @ModifiedName)";
                        cmd.Prepare();
                        cmd.Parameters.AddWithValue("@Name", currZone.Name);
                        cmd.Parameters.AddWithValue("@TypeID", currZone.TypeID);
                        cmd.Parameters.AddWithValue("@Perimeter", currZone.Perimeter);
                        cmd.Parameters.AddWithValue("@CellIds", currZone.CellIds);
                        cmd.Parameters.AddWithValue("@Location", currZone.Location);
                        cmd.Parameters.AddWithValue("@CompanyID", coyId);
                        cmd.Parameters.AddWithValue("@CreatedDate", DateTime.UtcNow);
                        cmd.Parameters.AddWithValue("@LastModified", DateTime.UtcNow);
                        cmd.Parameters.AddWithValue("@CreatedID", currZone.Created_UserID);
                        cmd.Parameters.AddWithValue("@ModifiedID", currZone.Modified_UserID);
                        cmd.Parameters.AddWithValue("@CreatedName", currZone.Created_User);
                        cmd.Parameters.AddWithValue("@ModifiedName", currZone.Modified_User);
                        cmd.ExecuteNonQuery();
                        conn.Close();
                    }
                }
            }
            catch (Exception ex)
            {
                Logger.LogEvent(ex.Message + "-Add(ZoneRepository)", System.Diagnostics.EventLogEntryType.Error);
            }

            return currZone;
        }

        public bool Remove(int id)
        {
            bool retVal = false;
            string query = string.Format("DELETE FROM zones WHERE zone_id = {0}", id);

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
                        conn.Close();
                    }
                }
            }
            catch (Exception ex)
            {
                Logger.LogEvent(ex.Message + "-Remove(ZoneRepository)", System.Diagnostics.EventLogEntryType.Error);
            }
            return retVal;
        }

        public bool Update(ZoneInfo currZone)
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

                        //get company id
                        int coyId = RepoHelpers.GetCompanyId(currZone.Company);
                        if (coyId < 0)
                        {
                            currZone.ErrorMessage = Consts.ERR_COMPANYERROR;
                            return false;
                        }

                        cmd.CommandText = "UPDATE zones SET name = @Name, type_id = @TypeID, perimeter = @Perimeter, " +
                                                "cell_ids = @CellIds, loc = @Location, company_id = @CompanyID, " +
                                                "modified_date = @LastModified, modified_by = @ModifiedID, modified_byName = @ModifiedName " +
                                                "WHERE zone_id = @ZoneID";
                        cmd.Prepare();
                        cmd.Parameters.AddWithValue("@Name", currZone.Name);
                        cmd.Parameters.AddWithValue("@TypeID", currZone.TypeID);
                        cmd.Parameters.AddWithValue("@Perimeter", currZone.Perimeter);
                        cmd.Parameters.AddWithValue("@CellIds", currZone.CellIds);
                        cmd.Parameters.AddWithValue("@Location", currZone.Location);
                        cmd.Parameters.AddWithValue("@CompanyID", coyId);
                        //cmd.Parameters.AddWithValue("@CreatedDate", currZone.CreatedDate); //once created, cannot be changed
                        cmd.Parameters.AddWithValue("@LastModified", DateTime.UtcNow);
                        cmd.Parameters.AddWithValue("@ModifiedID", currZone.Modified_UserID);
                        cmd.Parameters.AddWithValue("@ModifiedName", currZone.Modified_User);
                        cmd.Parameters.AddWithValue("@ZoneID", currZone.ZoneID);

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
                Logger.LogEvent(ex.Message + "-Update(ZoneRepository)", System.Diagnostics.EventLogEntryType.Error);
            }

            return retVal;
        }
    }
}