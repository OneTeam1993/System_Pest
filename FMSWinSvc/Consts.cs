﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace FMSWinSvc
{
    public class Consts
    {
        //GG400 
        public const string GG400WT_HEAD = "STX";
        public const string GG400WT_DATA = "$GPRMC";

        //Coban103A
        public const string COBAN103A_HEAD = "imei";
        //public const string LOGFILE = DateTime.Now.ToString("ddMMMyy") + "_logs.txt";

        //GG900 
        public const string GG900_HEAD = "$MG";
        //public const string GG900_DATA = "$MG";

        //MDT
        public const string MDT_HEAD = "$$ADS$";

        //AT07
        public const string AT07_HEAD = "$$";

        //N32A
        public const string N32A_HEAD = "$$KHN$";
        //public const string N32A_DATA = "";

        //AVL301
        public const string AVL301_HEAD = "24";
        //public const string AVL301_DATA = "";

        //GT06N
        public const string GT06N_HEAD = "7878";

        //MobileEye
        public const string MobileEye_HEAD = "L";
        //public const string MobileEye_DATA = "";

        //MDVR
        public const string MDVR_HEAD = "&";
        //public const string MDVR_DATA = "";

        //iRevo
        public const string iRevo_HEAD = "iRevo";
        //public const string iRevo_DATA = "";
    }
}
