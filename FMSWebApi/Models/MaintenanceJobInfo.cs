using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace FMSWebApi.Models
{
    public class MaintenanceJobInfo
    {
        public long MaintenanceJobID { get; set; }
        public long MaintenanceID { get; set; }
        public DateTime AlertDate { get; set; }
        public DateTime Timestamp { get; set; }
        public DateTime RxTime { get; set; }
        public int Flag { get; set; }
        public string FlagValue { get; set; }
        public DateTime JobCancelled { get; set; }
        public string CancelRemarks { get; set; }
        public int isSent { get; set; }
        public string ParamFlag { get; set; }
        public List<FindingsMaintenanceInfo> Findings { get; set; }
        public string ReferenceNo { get; set; }
        public string TechSignatures { get; set; }
        public string TechFillSignatures { get; set; }
        public string ImageCamera { get; set; }
        public string ImageFillCamera { get; set; }
		public string ErrorMessage { get; set; }      
    }
}