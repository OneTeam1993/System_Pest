﻿using MySql.Data.MySqlClient;
using FMSWebApi.Models;
using FMSWebApi.Repository;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using System.Web.Http.Cors;

namespace FMSWebApi.Controllers
{
    [EnableCors(origins: "*", headers: "*", methods: "*")]
    public class SaveLoginController : ApiController
    {
        private static readonly IEventRepository repository = new EventRepository();


        public string Get()
        {
            return "Save Login Events";
        }

        public EventInfo Post([FromBody]EventInfo item)
        {
            //Logger.LogEvent(string.Format("Tag: {0}", item.Tag), System.Diagnostics.EventLogEntryType.Error);
            item = repository.SaveLoginEvent(item);
            return item;
        }

    }
}
