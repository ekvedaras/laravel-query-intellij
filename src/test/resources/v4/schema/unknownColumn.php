<?php Schema::table('users', function (\Illuminate\Database\Schema\Blueprint $table) {
    $table->renameColumn('old_email', 'email');
});
