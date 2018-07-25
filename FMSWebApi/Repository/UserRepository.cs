﻿using FMSWebApi.Models;
using MySql.Data.MySqlClient;
using System;
using System.Collections.Generic;
using System.Configuration;
using System.Data;
using System.IO;
using System.Linq;
using System.Web;
using System.Web.Hosting;

namespace FMSWebApi.Repository
{
    public class UserRepository : IUserRepository
    {
        //private string mConnStr = "server=127.0.0.1;uid=root;pwd=12345;database=test;";
        private string mConnStr = "server=localhost;uid=root;pwd=@c3c0M;database=sp;max pool size=500;";
        private string mProjName = "SP";
        public IEnumerable<UserInfo> GetAll()
        {
            MySqlConnection conn = new MySqlConnection();
            MySqlCommand cmd = new MySqlCommand();
            List<UserInfo> arrUsers = new List<UserInfo>();

            try
            {
                conn.ConnectionString = mConnStr;
                conn.Open();

                cmd.CommandText = "view_users";
                cmd.Connection = conn;
                cmd.CommandType = CommandType.TableDirect;
                using (MySqlDataReader reader = cmd.ExecuteReader())
                {

                    if ((reader != null) && (reader.HasRows))
                    {
                        while (reader.Read())
                        {
                            UserInfo currUser = DataMgrTools.BuildUser(reader);
                            string strFill = "";
                            currUser.Image = GetImage(String.Format("users/{0}", currUser.UserID), ref strFill);
                            currUser.ImageFill = strFill;
                            arrUsers.Add(currUser);
                        }
                    }
                }
                
            }
            catch (MySqlException ex)
            {
                //Logger.LogEvent(mProjName, ex.Message, System.Diagnostics.EventLogEntryType.Error);
                Logger.LogEvent("view_users: " + ex.Message, System.Diagnostics.EventLogEntryType.Error);
            }

            

            if (conn != null)
                conn.Close();

            return arrUsers.ToArray();
        }
        public IEnumerable<UserInfo> GetAllUser()
        {
            MySqlConnection conn = new MySqlConnection();
            MySqlCommand cmd = new MySqlCommand();
            List<UserInfo> arrUsers = new List<UserInfo>();

            return arrUsers.ToArray();
        }
        public IEnumerable<UserInfo> GetByUser(UserInfo param)
        {

            List<UserInfo> arrUsers = new List<UserInfo>();
            UserInfo currUser = new UserInfo();
            object objTemp = new object();

            string query = "SELECT * FROM view_users WHERE user_name = @User";

            using (MySqlConnection conn = new MySqlConnection(mConnStr))
            {

                try
                {
                    using (MySqlCommand cmd = new MySqlCommand(query, conn))
                    {
                        conn.Open();
                        cmd.Prepare();
                        cmd.Parameters.AddWithValue("@User", param.User);

                        using (MySqlDataReader reader = cmd.ExecuteReader())
                        {
                            if ((reader != null) && (reader.HasRows))
                            {
                                while (reader.Read())
                                {
                                    currUser = DataMgrTools.BuildUser(reader);
                                    arrUsers.Add(currUser);
                                }
                            }
                        }

                        conn.Close();
                    }
                }
                catch (Exception ex)
                {
                    Logger.LogEvent(ex.Message + "-Get(GetByUser)", System.Diagnostics.EventLogEntryType.Error);

                }


            }

            return arrUsers;
        }
        public IEnumerable<UserInfo> GetByCompany(UserInfo param)
        {

            List<UserInfo> arrUsers = new List<UserInfo>();
            UserInfo currUser = new UserInfo();
            object objTemp = new object();

            //string query = "";

            string query = "SELECT * FROM view_users WHERE reseller_id = @ResellerID";

            if (param.CompanyID > 0) query += " and company_id = @CompanyID";

            if (param.RoleID > 0) query += " and role_id >= @RoleID";

            using (MySqlConnection conn = new MySqlConnection(mConnStr))
            {

                try
                {
                    using (MySqlCommand cmd = new MySqlCommand(query, conn))
                    {
                        conn.Open();
                        cmd.Prepare();
                        cmd.Parameters.AddWithValue("@CompanyID", param.CompanyID);
                        cmd.Parameters.AddWithValue("@RoleID", param.RoleID);
                        cmd.Parameters.AddWithValue("@ResellerID", param.ResellerID);

                        using (MySqlDataReader reader = cmd.ExecuteReader())
                        {
                            if ((reader != null) && (reader.HasRows))
                            {
                                while (reader.Read())
                                {
                                    currUser = DataMgrTools.BuildUser(reader);
                                    string strFill = "";
                                    currUser.Image = GetImage(String.Format("users/{0}", currUser.UserID), ref strFill);
                                    currUser.ImageFill = strFill;
                                    arrUsers.Add(currUser);
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


            }

            return arrUsers;
        }

        public UserInfo Get(int userID)
        {
            UserInfo currUser = new UserInfo();
            string query = string.Format("SELECT * FROM view_users WHERE user_id = {0}", userID);

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
                            currUser = DataMgrTools.BuildUser(reader);
                        }
                        conn.Close();
                    }
                }
                catch (Exception ex)
                {
                    Logger.LogEvent(ex.Message + "-Get(UserRepository)", System.Diagnostics.EventLogEntryType.Error);
                    conn.Close();
                }
            }
            return currUser;
        }

        public UserInfo Add(UserInfo currUser)
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
                        //int coyId = RepoHelpers.GetCompanyID(currUser.Company);
                        //if (coyId < 0)
                        //{
                        //    currUser.ErrorMessage = Consts.ERR_COMPANYERROR;
                        //    return currUser;
                        //}


                        //get role id
                        //int roleId = RepoHelpers.GetRoleId(currUser.RoleDesc);
                        //if (roleId <= 0)
                        //{
                        //    currUser.ErrorMessage = Consts.ERR_USER_ROLEERROR;
                        //    return currUser;
                        //}


                        //get language id
                        int langId = RepoHelpers.GetLangId(currUser.Language);
                        if (langId <= 0)
                        {
                            Logger.LogEvent("Language Id(User Repository): " + currUser.Language, System.Diagnostics.EventLogEntryType.Information);//testing
                            currUser.ErrorMessage = Consts.ERR_USER_LANGUAGEERROR;
                            return currUser;
                        }

                        Logger.LogEvent("Entered Repository: " + currUser.Name, System.Diagnostics.EventLogEntryType.Information);//testing
                        cmd.CommandText = "INSERT INTO users (name, user_name, password, role_id, phone, email, company_id, assets, notifications, " +
                            "login_retry, reports, language_id, api_key, reseller_id) " +
                            "VALUES (@Name, @UserName, @Password, @RoleID, @Phone, @Email, @CompanyID, @Assets, @Notifications, @LoginRetry, @Reports, @LanguageID, @ApiKey, @ResellerID)";

                        cmd.Prepare();
                        cmd.Parameters.AddWithValue("@Name", currUser.Name);
                        cmd.Parameters.AddWithValue("@UserName", currUser.User);
                        cmd.Parameters.AddWithValue("@Password", currUser.Password);
                        cmd.Parameters.AddWithValue("@RoleID", currUser.RoleID);
                        cmd.Parameters.AddWithValue("@Phone", currUser.Phone);
                        cmd.Parameters.AddWithValue("@Email", currUser.Email);
                        //cmd.Parameters.AddWithValue("@CompanyID", coyId);
                        cmd.Parameters.AddWithValue("@CompanyID", currUser.CompanyID);
                        cmd.Parameters.AddWithValue("@Assets", currUser.Assets);
                        cmd.Parameters.AddWithValue("@Notifications", currUser.Notifications);
                        cmd.Parameters.AddWithValue("@LoginRetry", currUser.LoginRetry);
                        cmd.Parameters.AddWithValue("@Reports", currUser.Reports);
                        cmd.Parameters.AddWithValue("@LanguageID", langId);
                        cmd.Parameters.AddWithValue("@ApiKey", currUser.ApiKey);
                        cmd.Parameters.AddWithValue("@ResellerID", currUser.ResellerID);

                        cmd.ExecuteNonQuery();
                        conn.Close();
                    }
                }
            }
            catch (Exception ex)
            {
                Logger.LogEvent(ex.Message + "-Add(UserInfo)", System.Diagnostics.EventLogEntryType.Error);
            }

            return currUser;
        }

        public bool Remove(int userID)
        {
            bool retVal = false;
            string query = string.Format("DELETE FROM users WHERE user_id = {0}", userID);

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
                    Logger.LogEvent(ex.Message + "-GetUserId", System.Diagnostics.EventLogEntryType.Error);
                  
                }

                DeleteImage(String.Format("users/{0}", userID));
            }

            return retVal;
        }

        public bool Update(UserInfo currUser)
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

                        int coyId = currUser.CompanyID;
                        int roleId = currUser.RoleID;
                        int langId = currUser.LanguageID;

                        try
                        {
                            //get company id
                            
                            //if (coyId <= 0)
                            //{
                            //    coyId = RepoHelpers.GetCompanyID(currUser.Company);
                            //    if (coyId < 0)
                            //    {
                            //        currUser.ErrorMessage = Consts.ERR_COMPANYERROR;
                            //        return false;
                            //    }
                            //}

                            //get role id
                            if (roleId <= 0)
                            {
                                roleId = RepoHelpers.GetRoleId(currUser.RoleDesc);
                                if (roleId <= 0)
                                {
                                    currUser.ErrorMessage = Consts.ERR_USER_ROLEERROR;
                                    return false;
                                }
                            }

                            //get language id
                            if (langId <= 0)
                            {
                                langId = RepoHelpers.GetLangId(currUser.Language);
                                if (langId <= 0)
                                {
                                    currUser.ErrorMessage = Consts.ERR_USER_LANGUAGEERROR;
                                    return false;
                                }
                            }
                        }
                        catch(Exception ex)
                        {
                            Logger.LogEvent(ex.Message + "-Update(Values Validation)", System.Diagnostics.EventLogEntryType.Error);
                            return false;
                        }

                        cmd.CommandText = "UPDATE users SET name = @Name, user_name = @UserName, password = @Password, role_id = @RoleID, " +
                            "phone = @Phone, email = @Email, company_id = @CompanyID, assets = @Assets, notifications = @Notifications, " +
                                            "login_retry = @LoginRetry, reports = @Reports, language_id = @LanguageID, api_key = @ApiKey, reseller_id = @ResellerID WHERE user_id = @UserID";
                        cmd.Prepare();
                        cmd.Parameters.AddWithValue("@Name", currUser.Name);
                        cmd.Parameters.AddWithValue("@UserName", currUser.User);
                        cmd.Parameters.AddWithValue("@Password", currUser.Password);
                        //cmd.Parameters.AddWithValue("@RoleID", roleId);
                        cmd.Parameters.AddWithValue("@RoleID", currUser.RoleID);
                        cmd.Parameters.AddWithValue("@Phone", currUser.Phone);
                        cmd.Parameters.AddWithValue("@Email", currUser.Email);
                        //cmd.Parameters.AddWithValue("@CompanyID", coyId);
                        cmd.Parameters.AddWithValue("@CompanyID", currUser.CompanyID);
                        cmd.Parameters.AddWithValue("@Assets", currUser.Assets);
                        cmd.Parameters.AddWithValue("@Notifications", currUser.Notifications);
                        cmd.Parameters.AddWithValue("@LoginRetry", currUser.LoginRetry);
                        cmd.Parameters.AddWithValue("@Reports", currUser.Reports);
                        cmd.Parameters.AddWithValue("@LanguageID", langId);
                        cmd.Parameters.AddWithValue("@ApiKey", currUser.ApiKey);
                        cmd.Parameters.AddWithValue("@ResellerID", currUser.ResellerID);
                        cmd.Parameters.AddWithValue("@UserID", currUser.UserID);

                        //cmd.ToString();
                        //HINT: Missing Param here
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
                retVal = false;
                Logger.LogEvent(ex.Message + "-Update(UserRepository)", System.Diagnostics.EventLogEntryType.Error);
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
                Logger.LogEvent(ex.Message + "-(Get Image)", System.Diagnostics.EventLogEntryType.Error);
            }

            // image file not found
            strFill = "None";
            return strName;
        }

        /// <summary>
        /// deletes image file
        /// </summary>
        /// <param name="strName"></param>
        public void DeleteImage(string strName)
        {
            try
            {
                // loop through image file types
                List<string> arrTypes = new List<string>() { "jpg", "png", "gif", "bmp" };
                foreach (string strType in arrTypes)
                {
                    // check file path
                    string strFile = String.Format("{0}images\\{1}.{2}",
                        HostingEnvironment.ApplicationPhysicalPath, strName, strType);
                    if (File.Exists(strFile))
                    {
                        // return image path
                        File.Delete(strFile);
                        break;
                    }
                }
            }
            catch (Exception ex)
            {
                // log error
                Logger.LogEvent(ex.Message + "-(Delete Image)", System.Diagnostics.EventLogEntryType.Error);
            }
        }
    }
}