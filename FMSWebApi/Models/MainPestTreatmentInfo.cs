using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace FMSWebApi.Models
{
    /// <summary>
    /// zone info
    /// </summary>
    public class MainPestTreatmentInfo
    {
        public long MainPestTreatmentID { get; set; }
        public long MaintenanceID { get; set; }
        public string PestDesc { get; set; }
        public string TreatmentDesc { get; set; }
        public int ItemNo { get; set; }

    }
}