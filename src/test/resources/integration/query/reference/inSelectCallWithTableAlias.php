<?php

\DB::query()->from('testProject1.users as u1')->select('u1.first_name');
