﻿using FMSWebApi.Models;
using FMSWebApi.Repository;
using System;
using System.Collections.Generic;
using System.Configuration;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web;
using System.Web.Hosting;
using System.Web.Http;
using System.Web.Http.Cors;
using System.Web.UI.WebControls;




namespace FMSWebApi.Controllers
{
    [EnableCors(origins: "*", headers: "*", methods: "*")]
    public class CheckDeviceController : ApiController
    {
        private static readonly IDeviceRepository repository = new DeviceRepository();

        public IEnumerable<DeviceInfo> GetByDevice([FromUri]DeviceInfo param)
        {

            if (!string.IsNullOrEmpty(param.Name))
            {

                return repository.GetByDevice(param);
            }
            else
            {
                return repository.GetAllDevice();
            }
        }

    }  
}
